package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.adapter.AntdAdapter;
import vn.candicode.exceptions.EntityNotFoundException;
import vn.candicode.exceptions.FileCannotReadException;
import vn.candicode.exceptions.FileCannotStoreException;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.requests.TestcaseRequest;
import vn.candicode.payloads.requests.TestcasesRequest;
import vn.candicode.payloads.responses.Challenge;
import vn.candicode.payloads.responses.ChallengeDetails;
import vn.candicode.payloads.responses.SourceCodeStructure;
import vn.candicode.payloads.responses.Testcase;
import vn.candicode.repositories.ChallengeConfigRepository;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.TestcaseRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;
import vn.candicode.services.StorageService;
import vn.candicode.utils.DatetimeUtils;
import vn.candicode.utils.FileUtils;
import vn.candicode.utils.PreloadEntities;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static vn.candicode.services.StorageService.Factor.CHALLENGE;

@Service
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final StorageService storageService;

    private final ChallengeRepository challengeRepository;
    private final ChallengeConfigRepository challengeConfigRepository;
    private final TestcaseRepository testcaseRepository;

    @Autowired
    private PreloadEntities preloadEntities;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeServiceImpl(StorageService storageService,
                                ChallengeRepository challengeRepository,
                                ChallengeConfigRepository challengeConfigRepository,
                                TestcaseRepository testcaseRepository) {
        this.storageService = storageService;
        this.challengeRepository = challengeRepository;
        this.challengeConfigRepository = challengeConfigRepository;
        this.testcaseRepository = testcaseRepository;
    }

    /**
     * @param payload
     * @return id of the newly created challenge
     */
    @Override
    @Transactional
    public Long createChallenge(NewChallengeRequest payload, UserPrincipal currentUser) {
        try {
            String bannerPath = storageService.storeChallengeBanner(payload.getBanner(), currentUser.getUserId());

            ChallengeEntity challenge = new ChallengeEntity();

            challenge.setTitle(payload.getTitle());
            challenge.setDescription(payload.getDescription());
            challenge.setTestcaseInputFormat(payload.getTcInputFormat());
            challenge.setTestcaseOutputFormat(payload.getTcOutputFormat());
            challenge.setLevel(ChallengeLevel.valueOf(payload.getLevel().toUpperCase()));
            challenge.setPoint(calculateChallengePoint(challenge.getLevel()));
            challenge.setAuthor(currentUser.getEntityRef());
            challenge.setBanner(bannerPath);

            entityManager.persist(challenge);

            ChallengeConfigEntity challengeConfig = new ChallengeConfigEntity();

            challengeConfig.setChallenge(challenge);
            challengeConfig.setLanguage(preloadEntities.getLanguageEntities().get(LanguageName.valueOf(payload.getLanguage().toUpperCase())));
            challengeConfig.setChallengeDir(payload.getChallengeDir());
            challengeConfig.setImplementedPath(storageService.cleanPath(payload.getImplementedPath(), CHALLENGE, currentUser.getUserId(), payload.getChallengeDir()));
            challengeConfig.setNonImplementedPath(storageService.cleanPath(payload.getNonImplementedPath(), CHALLENGE, currentUser.getUserId(), payload.getChallengeDir()));
            challengeConfig.setRunPath(storageService.cleanPath(payload.getRunPath(), CHALLENGE, currentUser.getUserId(), payload.getChallengeDir()));

            if (payload.getCompilePath() != null) {
                challengeConfig.setCompilePath(storageService.cleanPath(payload.getCompilePath(), CHALLENGE, currentUser.getUserId(), payload.getChallengeDir()));
            }

            entityManager.persist(challengeConfig);

            return challenge.getChallengeId();
        } catch (IOException e) {
            log.error("I/O Exception. Message - {}", e.getLocalizedMessage());
            throw new FileCannotStoreException(e.getLocalizedMessage());
        } catch (EntityExistsException e) {
            log.error("Entity has already existing. Message - {}", e.getLocalizedMessage());
            throw new PersistenceException(e.getLocalizedMessage());
        }
    }

    private Integer calculateChallengePoint(ChallengeLevel level) {
        switch (level) {
            case HARD:
                return 300;
            case MODERATE:
                return 200;
            case EASY:
                return 100;
            default:
                return 0;
        }
    }

    @Override
    public SourceCodeStructure storeChallengeSourceCode(MultipartFile zipFile, UserPrincipal currentUser) {
        try {
            String challengeDir = storageService.storeChallengeSourceCode(zipFile, currentUser.getUserId());
            String challengeDirname = challengeDir.substring(challengeDir.lastIndexOf(File.separator));

            SourceCodeStructure payload = new SourceCodeStructure();
            payload.setRoot(challengeDirname);
            payload.setNodes(AntdAdapter.fromNodes(storageService.getDirectoryTree(challengeDir)));

            return payload;
        } catch (IOException e) {
            log.error("I/O Exception. Message - {}", e.getLocalizedMessage());
            throw new FileCannotStoreException(e.getLocalizedMessage());
        }
    }

    @Override
    @Transactional
    public Integer createTestcases(Long challengeId, TestcasesRequest payload, UserPrincipal currentUser) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        int previousNumTestcases = challenge.getTestcases().size();

        Pattern testcaseInputValidator = Pattern.compile(challenge.getTestcaseInputFormat());
        Pattern testcaseOutputValidator = Pattern.compile(challenge.getTestcaseOutputFormat());

        for (TestcaseRequest testcaseDto : payload.getTestcases()) {
            if (testcaseInputValidator.matcher(testcaseDto.getInput()).matches() &&
                testcaseOutputValidator.matcher(testcaseDto.getOutput()).matches()) {
                challenge.addTestcase(new TestcaseEntity(testcaseDto.getInput(), testcaseDto.getOutput(), testcaseDto.getHidden()));
            }
        }

        entityManager.persist(challenge);

        return challenge.getTestcases().size() - previousNumTestcases;
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeDetails getChallengeDetails(Long challengeId) {
        ChallengeEntity challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        ChallengeDetails challengeDetails = new ChallengeDetails();

        challengeDetails.setChallengeId(challenge.getChallengeId());
        challengeDetails.setTitle(challenge.getTitle());
        challengeDetails.setDescription(challenge.getDescription());
        challengeDetails.setLevel(challenge.getLevel().name());
        challengeDetails.setPoint(challenge.getPoint());
        challengeDetails.setAuthor(challenge.getAuthor().getFullName());
        challengeDetails.setBanner(challenge.getBanner());
        challengeDetails.setCreatedAt(challenge.getCreatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
        challengeDetails.setUpdatedAt(challenge.getUpdatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));

        List<ChallengeConfigEntity> challengeConfigs = challengeConfigRepository.findAllByChallenge(challenge);

        for (ChallengeConfigEntity challengeConfig : challengeConfigs) {
            Challenge content = new Challenge();
            content.setLanguage(challengeConfig.getLanguage().getName().name());

            String nonImplementedPath = storageService.getNonImplementedPathByChallengeAndConfig(challenge, challengeConfig);

            try {
                content.setText(FileUtils.readFileToString(new File(nonImplementedPath)));
            } catch (IOException e) {
                throw new FileCannotReadException("Some challenge contents cannot be read");
            }

            challengeDetails.getContents().add(content);
        }

        List<TestcaseEntity> testcases = testcaseRepository.findAllByChallenge(challenge);

        for (TestcaseEntity testcase : testcases) {
            challengeDetails.getTestcases().add(new Testcase(
                testcase.getInput(), testcase.getExpectedOutput(), testcase.getHidden()));
        }

        return challengeDetails;
    }
}

package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.adapter.AntdAdapter;
import vn.candicode.common.structure.wrapper.Triple;
import vn.candicode.core.SimpleVerdict;
import vn.candicode.core.Verdict;
import vn.candicode.exceptions.*;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;
import vn.candicode.models.dtos.ChallengeLanguageDTO;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.requests.*;
import vn.candicode.payloads.responses.*;
import vn.candicode.repositories.*;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;
import vn.candicode.services.StorageService;
import vn.candicode.utils.DatetimeUtils;
import vn.candicode.utils.FileUtils;
import vn.candicode.utils.PreloadEntities;
import vn.candicode.utils.RegexUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static vn.candicode.services.StorageService.Factor.CHALLENGE;

@Service
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final StorageService storageService;

    private final ChallengeRepository challengeRepository;
    private final ChallengeConfigRepository challengeConfigRepository;
    private final TestcaseRepository testcaseRepository;
    private final SubmissionRepository submissionRepository;
    private final CommentRepository commentRepository;

    @Autowired
    private PreloadEntities preloadEntities;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeServiceImpl(StorageService storageService,
                                ChallengeRepository challengeRepository,
                                ChallengeConfigRepository challengeConfigRepository,
                                TestcaseRepository testcaseRepository,
                                SubmissionRepository submissionRepository,
                                CommentRepository commentRepository) {
        this.storageService = storageService;
        this.challengeRepository = challengeRepository;
        this.challengeConfigRepository = challengeConfigRepository;
        this.testcaseRepository = testcaseRepository;
        this.submissionRepository = submissionRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * @param payload
     * @return id of the newly created challenge
     */
    @Override
    @Transactional
    public Long createChallenge(NewChallengeRequest payload, UserPrincipal currentUser) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.storeChallengeBanner(payload.getBanner(), currentUser.getUserId());
            }

            ChallengeEntity challenge = new ChallengeEntity();

            challenge.setTitle(payload.getTitle());
            challenge.setDescription(payload.getDescription());
            challenge.setTestcaseInputFormat(RegexUtils.generateRegex(payload.getTcInputFormat()));
            challenge.setTestcaseOutputFormat(RegexUtils.generateRegex(payload.getTcOutputFormat()));
            challenge.setLevel(ChallengeLevel.valueOf(payload.getLevel()));
            challenge.setPoint(calculateChallengePoint(challenge.getLevel()));
            challenge.setAuthor(currentUser.getEntityRef());
            challenge.setBanner(bannerPath);

            entityManager.persist(challenge);

            ChallengeConfigEntity challengeConfig = new ChallengeConfigEntity();

            challengeConfig.setChallenge(challenge);
            challengeConfig.setLanguage(preloadEntities.getLanguageEntities().get(LanguageName.valueOf(payload.getLanguage())));
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
            case Hard:
                return 300;
            case Moderate:
                return 200;
            case Easy:
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
            payload.setChallengeDir(challengeDirname);
            payload.setChildren(AntdAdapter.fromNodes(storageService.getDirectoryTree(challengeDir)));

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

        try {
            FileUtils.appendToFile(
                new File(storageService.getTestcaseInputPathByChallenge(challenge.getChallengeId())),
                challenge.getTestcases().stream()
                    .map(TestcaseEntity::getInput)
                    .collect(Collectors.toList())
            );
        } catch (IOException e) {
            log.error("I/O Exception. Message - {}", e.getLocalizedMessage());
            throw new FileCannotStoreException(e.getLocalizedMessage());
        }

        return challenge.getTestcases().size() - previousNumTestcases;
    }

    @Override
    @Transactional
    public TestcaseVerificationResult verifyTestcase(Long challengeId, TestcaseVerificationRequest payload) {
        TestcaseVerificationResult result = new TestcaseVerificationResult();

        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        Pattern testcaseInputFormatValidator = Pattern.compile(challenge.getTestcaseInputFormat());

        if (!testcaseInputFormatValidator.matcher(payload.getInput()).matches()) {
            result.setValidFormat(false);
            result.setValidFormatError("Input should be " + getTestcaseFormat(challenge));
            return result;
        }

        result.setValidFormat(true);

        ChallengeConfigEntity config = challengeConfigRepository.findByChallengeAndLanguage(challengeId, preloadEntities.getLanguageEntities().get(LanguageName.valueOf(payload.getLanguage())))
            .orElseThrow(() -> new EntityNotFoundException("Challenge Config", "challengeId and language", challengeId + " - " + payload.getLanguage()));

        String challengeDir = storageService.getChallengeDirPathByChallengeAuthorAndConfig(challenge.getAuthor().getUserId(), config);

        SimpleVerdict verdict = new SimpleVerdict(payload.getLanguage(), payload.getInput(), challengeDir);

        Triple verifyResult = verdict.verify();

        result.setCompiled(verifyResult.isCompiled());
        result.setCompileError(verifyResult.getCompileError());
        result.setOutput(verifyResult.getOutput());

        return result;
    }

    private String getTestcaseFormat(ChallengeEntity challenge) {
        try {
            return StringUtils.collectionToCommaDelimitedString(RegexUtils.resolveRegex(challenge.getTestcaseInputFormat()));
        } catch (RegexTemplateNotFoundException ignored) {
        }
        return null;
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

        try {
            challengeDetails.setTcInputFormat(new TestcaseFormat(RegexUtils.resolveRegex(challenge.getTestcaseInputFormat())));
            challengeDetails.setTcOutputFormat(new TestcaseFormat(RegexUtils.resolveRegex(challenge.getTestcaseOutputFormat())));
        } catch (RegexTemplateNotFoundException e) {
            log.error("Cannot resolve testcase format");
        }

        List<ChallengeConfigEntity> challengeConfigs = challengeConfigRepository.findAllByChallenge(challenge);

        for (ChallengeConfigEntity challengeConfig : challengeConfigs) {
            Challenge content = new Challenge();
            content.setLanguage(challengeConfig.getLanguage().getText().name());

            String nonImplementedPath = storageService.getNonImplementedPathByAuthorAndConfig(challenge.getAuthor().getUserId(), challengeConfig);

            try {
                content.setText(FileUtils.readFileToString(new File(nonImplementedPath)));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new FileCannotReadException("Some challenge contents cannot be read");
            }

            challengeDetails.getContents().add(content);
            challengeDetails.getLanguages().add(challengeConfig.getLanguage().getText().name());
        }

        List<TestcaseEntity> testcases = testcaseRepository.findAllByChallenge(challenge);

        for (TestcaseEntity testcase : testcases) {
            challengeDetails.getTestcases().add(new Testcase(
                testcase.getInput(), testcase.getExpectedOutput(), testcase.getHidden()));
        }

        return challengeDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ChallengeSummary> getChallengeList(Pageable pageable) {
        Page<ChallengeEntity> challenges = challengeRepository.findAll(pageable);

        return getChallengeSummaryPaginatedResponse(challenges);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ChallengeSummary> getMyChallengeList(Pageable pageable, UserPrincipal currentUser) {
        Page<ChallengeEntity> challenges = challengeRepository.findAllByAuthor(currentUser.getEntityRef(), pageable);

        return getChallengeSummaryPaginatedResponse(challenges);
    }

    private PaginatedResponse<ChallengeSummary> getChallengeSummaryPaginatedResponse(Page<ChallengeEntity> challenges) {
        PaginatedResponse<ChallengeSummary> response = new PaginatedResponse<>();

        response.setPage(challenges.getNumber() + 1);
        response.setSize(challenges.getSize());
        response.setTotalElements(challenges.getTotalElements());
        response.setTotalPages(challenges.getTotalPages());
        response.setFirst(challenges.isFirst());
        response.setLast(challenges.isLast());

        List<ChallengeSummary> items = new ArrayList<>();

        for (ChallengeEntity challenge : challenges) {
            ChallengeSummary challengeSummary = new ChallengeSummary();
            challengeSummary.setAuthor(challenge.getAuthor().getFullName());
            challengeSummary.setBanner(challenge.getBanner());
            challengeSummary.setChallengeId(challenge.getChallengeId());
            challengeSummary.setCreatedAt(challenge.getCreatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            challengeSummary.setUpdatedAt(challenge.getUpdatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            challengeSummary.setLevel(challenge.getLevel().name());
            challengeSummary.setCategories(challenge
                .getCategories().stream().map(c -> c.getCategory().getText().name()).collect(Collectors.toList()));
            List<ChallengeLanguageDTO> languages = challengeConfigRepository.findLanguageListByChallenge(challenge);
            challengeSummary.setLanguages(languages.stream().map(l -> l.getText().name()).collect(Collectors.toList()));
            challengeSummary.setNumAttendees(submissionRepository.countAllByChallenge(challenge));
            challengeSummary.setTitle(challenge.getTitle());
            challengeSummary.setNumRates(10);
            challengeSummary.setRate(4.5f);
            challengeSummary.setPoint(challenge.getPoint());
            challengeSummary.setNumComments(commentRepository.countAllByChallenge(challenge));

            items.add(challengeSummary);
        }

        response.setItems(items);

        return response;
    }

    @Override
    @Transactional
    public SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal currentUser) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        ChallengeConfigEntity challengeConfig = challengeConfigRepository
            .findByChallengeAndLanguage(challenge.getChallengeId(), preloadEntities.getLanguageEntities().get(LanguageName.valueOf(payload.getLanguage())))
            .orElseThrow(() -> new EntityNotFoundException("Challenge config", "challengeId and language", challengeId + " and " + payload.getLanguage()));

        List<TestcaseEntity> testcases = challenge.getTestcases();

        File challengeDir = new File(storageService.getChallengeDirPathByChallengeAuthorAndConfig(challenge.getAuthor().getUserId(), challengeConfig));
        File submissionDir = new File(storageService.getSubmissionDirPathBySubmitterAndConfig(currentUser.getUserId(), challengeConfig));

        File originTestcaseInputFile = new File(storageService.getTestcaseInputPathByChallenge(challengeId));
        File copiedTestcaseInputFile = new File(storageService.getSubmissionDirPathBySubmitterAndConfig(currentUser.getUserId(), challengeConfig) + File.separator + FileUtils.INPUT_TESTCASE_FILE);

        File originImplementedFile = new File(storageService.getImplementedPathBySubmitterAndConfig(currentUser.getUserId(), challengeConfig));

        try {
            FileUtils.copyDir2Dir(challengeDir, submissionDir);
            FileUtils.copyFile2File(originTestcaseInputFile, copiedTestcaseInputFile);
            FileUtils.overwriteFile(originImplementedFile, payload.getCode());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileCannotReadException("Some challenge contents cannot be read");
        }

        CountDownLatch latch = new CountDownLatch(1);

        new Verdict(LanguageName.valueOf(payload.getLanguage()), submissionDir.getAbsolutePath(), latch).start();

        try {
            latch.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new CodeExecutionException("Cannot wait until get submission response. Message - " + e.getMessage());
        }

        File errorFile = new File(storageService.getErrorPathBySubmitterAndConfig(currentUser.getUserId(), challengeConfig));
        File outputFile = new File(storageService.getTestcaseOutputPathBySubmitterAndConfig(currentUser.getUserId(), challengeConfig));

        SubmissionResult result = new SubmissionResult();
        result.setTotal(testcases.size());

        try {
            if (!outputFile.exists() || outputFile.length() == 0) { // Compile error
                result.setPassed(0);
                result.setCompiled("Failed");
                result.setError(retrieveCompileError(errorFile));
            } else {
                List<String> actualOutputs = retrieveSubmissionResults(outputFile);
                Map<Long, String> runtimeErrors = retrieveRuntimeError(errorFile);
                int passed = 0;
                for (int i = 0, actualOutputsSize = actualOutputs.size(); i < actualOutputsSize; i++) {
                    TestcaseEntity testcase = testcases.get(i);
                    String actualOutput = actualOutputs.get(i);
                    String expectedOutput = testcase.getExpectedOutput();
                    if (expectedOutput.equals("Error")) {
                        result.getDetails().add(new TestcaseResult(
                            testcase.getHidden(),
                            testcase.getInput(),
                            testcase.getExpectedOutput(),
                            runtimeErrors.get(testcase.getTestcaseId())
                        ));
                    } else {
                        result.getDetails().add(new TestcaseResult(
                            testcase.getHidden(),
                            testcase.getInput(),
                            expectedOutput,
                            actualOutput
                        ));
                        if (expectedOutput.equals(actualOutput)) {
                            passed++;
                        }
                    }
                }
                result.setCompiled("Success");
                result.setPassed(passed);
            }
        } catch (IOException e) {
            log.error("I/O error happened when constructing the submission response. Message - {}", e.getMessage());
            throw new FileCannotReadException(e.getMessage());
        }

        return result;
    }

    private List<String> retrieveSubmissionResults(File outputFile) throws IOException {
        List<String> results = new ArrayList<>();

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));

        while (StringUtils.hasText(line = reader.readLine())) {
            results.add(line);
        }

        reader.close();

        return results;
    }

    private String retrieveCompileError(File errorFile) throws IOException {
        return FileUtils.readFileToString(errorFile);
    }

    private Map<Long, String> retrieveRuntimeError(File errorFile) throws IOException {
        Map<Long, String> errors = new HashMap<>();

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(errorFile));

        while (StringUtils.hasText(line = reader.readLine())) {
            String[] parts = line.split(":", 2);
            Long testcaseId = Long.parseLong(parts[0].split(" ")[1]);
            String error = parts[1];
            errors.put(testcaseId, error);
        }

        return errors;
    }

    @Override
    @Transactional
    public void editChallenge(Long challengeId, EditChallengeRequest payload) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        if (challengeRepository.existsByTitle(payload.getTitle())) {
            throw new PersistenceException("Challenge has been already exist with tile" + payload.getTitle());
        }

        challenge.setTitle(payload.getTitle());
        challenge.setLevel(ChallengeLevel.valueOf(payload.getLevel()));
        challenge.setDescription(payload.getDescription());

        String bannerPath;
        if (payload.getBanner() != null) {
            try {
                bannerPath = storageService.storeChallengeBanner(payload.getBanner(), challengeId);
                challenge.setBanner(bannerPath);
            } catch (IOException e) {
                log.error("Cannot store challenge banner for challenge with {}. Message - {}", challengeId, e.getMessage());
                throw new FileCannotStoreException(e.getMessage());
            }
        }

        challengeRepository.save(challenge);
    }

    @Override
    public void deleteChallenge(Long challengeId) {
        if (challengeRepository.existsById(challengeId)) {
            challengeRepository.deleteById(challengeId);
        }
    }
}

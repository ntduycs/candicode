package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.adapter.AntdAdapter;
import vn.candicode.core.Verdict;
import vn.candicode.exceptions.*;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.requests.TestcaseRequest;
import vn.candicode.payloads.requests.TestcasesRequest;
import vn.candicode.payloads.responses.*;
import vn.candicode.repositories.ChallengeConfigRepository;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.TestcaseRepository;
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
import java.util.Collection;
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

        try {
            FileUtils.appendToFile(
                new File(storageService.getTestcaseInputPathByChallenge(challenge.getChallengeId())),
                challenge.getTestcases().stream().map(tc -> {
                    int numArgs = tc.getInput().split("\\|").length;
                    return String.format("%s %s %s", tc.getTestcaseId(), numArgs, tc.getInput());
                }).collect(Collectors.toList())
            );
        } catch (IOException e) {
            log.error("I/O Exception. Message - {}", e.getLocalizedMessage());
            throw new FileCannotStoreException(e.getLocalizedMessage());
        }

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
            content.setLanguage(challengeConfig.getLanguage().getText().name());

            String nonImplementedPath = storageService.getNonImplementedPathByAuthorAndConfig(challenge.getAuthor().getUserId(), challengeConfig);

            try {
                content.setText(FileUtils.readFileToString(new File(nonImplementedPath)));
            } catch (IOException e) {
                log.error(e.getMessage());
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

    @Override
    @Transactional
    public SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal currentUser) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        ChallengeConfigEntity challengeConfig = challengeConfigRepository
            .findByChallengeAndLanguage(challenge.getChallengeId(), preloadEntities.getLanguageEntities().get(LanguageName.valueOf(payload.getLanguage())))
            .orElseThrow(() -> new EntityNotFoundException("Challenge config", "challengeId and language", challengeId + " and " + payload.getLanguage()));

        Collection<TestcaseEntity> testcases = challenge.getTestcases();

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
                Map<Long, String> actualOutputs = retrieveSubmissionResults(outputFile);
                Map<Long, String> runtimeErrors = retrieveRuntimeError(errorFile);
                int passed = 0;
                for (TestcaseEntity testcase : testcases) {
                    String expectedOutput = testcase.getExpectedOutput();
                    String actualOutput = actualOutputs.get(testcase.getTestcaseId());
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

    private Map<Long /*testcaseid*/, String /*testcaseoutput*/> retrieveSubmissionResults(File outputFile) throws IOException {
        Map<Long, String> results = new HashMap<>();

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));

        while (StringUtils.hasText(line = reader.readLine())) {
            String[] parts = line.split(":", 2);
            Long testcaseId = Long.parseLong(parts[0].split(" ")[1]);
            String actualOutput = parts[1];
            results.put(testcaseId, actualOutput);
        }

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
}

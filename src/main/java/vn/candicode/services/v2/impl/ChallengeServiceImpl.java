package vn.candicode.services.v2.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.candicode.core.Verdict;
import vn.candicode.exceptions.EntityNotFoundException;
import vn.candicode.exceptions.FileCannotReadException;
import vn.candicode.exceptions.FileCannotStoreException;
import vn.candicode.exceptions.RegexTemplateNotFoundException;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.requests.TestcaseVerificationRequest;
import vn.candicode.payloads.responses.SubmissionResult;
import vn.candicode.payloads.responses.TestcaseResult;
import vn.candicode.payloads.responses.TestcaseVerificationByLanguage;
import vn.candicode.payloads.responses.TestcaseVerificationResult;
import vn.candicode.repositories.ChallengeConfigRepository;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.TestcaseRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.v1.StorageService;
import vn.candicode.services.v2.ChallengeService;
import vn.candicode.utils.FileUtils;
import vn.candicode.utils.RegexUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service("challengeServiceV2")
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeConfigRepository challengeConfigRepository;
    private final TestcaseRepository testcaseRepository;

    private final StorageService storageService;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeConfigRepository challengeConfigRepository, TestcaseRepository testcaseRepository, StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.challengeConfigRepository = challengeConfigRepository;
        this.testcaseRepository = testcaseRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal user) {
        SubmissionResult submissionResult = new SubmissionResult();
        LanguageName language = LanguageName.valueOf(payload.getLanguage());

        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new EntityNotFoundException("Challenge", "challengeId", challengeId));

        ChallengeConfigEntity challengeConfig = challengeConfigRepository.findByChallengeAndLanguage(challengeId, language)
            .orElseThrow(() -> new EntityNotFoundException("Challenge Config", "challengeId and language", challengeId + " and " + language));

        List<TestcaseEntity> challengeTestcases = challenge.getTestcases();

        File challengeDir = new File(storageService.getChallengeDirPathByChallengeAuthorAndConfig(user.getUserId(), challengeConfig.getChallengeDir()));
        File submissionDir = new File(storageService.getSubmissionDir(user.getUserId(), challenge.getChallengeId().toString()));

        try {
            FileUtils.copyDir2Dir(challengeDir, submissionDir);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            throw new FileCannotReadException("Cannot copy challenge dir to submission dir");
        }

        try {
            File implementedFile = new File(storageService.getImplementedPathFromSubmissionDir(submissionDir.getAbsolutePath(), challengeConfig.getImplementedPath()));
            FileUtils.overwriteFile(implementedFile, payload.getCode());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            throw new FileCannotStoreException("Cannot overwrite submitted code to destination");
        }

        submissionResult.setTotal(challengeTestcases.size());

        int passedTestcases = 0;
        boolean hasCompiled = false;
        String root = submissionDir.getAbsolutePath() + challengeConfig.getCompilePath().substring(0, challengeConfig.getCompilePath().lastIndexOf(File.separator));
        for (TestcaseEntity testcase : challengeTestcases) {
            try {
                File inputFile = new File(root, FileUtils.INPUT_TESTCASE_FILE);
                FileUtils.overwriteFile(inputFile, testcase.getInput());
                CountDownLatch timer = new CountDownLatch(1);
                new Verdict(language, submissionDir.getAbsolutePath(), challengeConfig.getCompilePath(), challengeConfig.getRunPath(), timer, hasCompiled).start();
                timer.await(3000, TimeUnit.MILLISECONDS);

                if (!hasCompiled) {
                    File errorFile = new File(root, FileUtils.ERROR_FILE);
                    if (errorFile.exists()) {
                        String compileError = FileUtils.readFileToString(errorFile);
                        submissionResult.setCompiled("Failed");
                        submissionResult.setError(compileError);
                        submissionResult.setDetails(new ArrayList<>());
                        submissionResult.setPassed(0);
                        org.apache.commons.io.FileUtils.deleteQuietly(errorFile);
                        return submissionResult;
                    } else {
                        hasCompiled = true;
                        submissionResult.setCompiled("Success");
                    }
                }

                File outputFile = new File(root, "out.txt");

                if (outputFile.exists()) {
                    String output = FileUtils.readFileToString(outputFile);
                    if (StringUtils.hasText(output)) {
                        submissionResult.getDetails().add(new TestcaseResult(testcase.getHidden(), testcase.getInput(), testcase.getExpectedOutput(), output, true));
                        if (testcase.getExpectedOutput().equals(output)) passedTestcases++;
                        org.apache.commons.io.FileUtils.deleteQuietly(outputFile);
                    } else {
                        File errorFile = new File(root, "err.txt");
                        String error = FileUtils.readFileToString(errorFile);
                        submissionResult.getDetails().add(new TestcaseResult(testcase.getHidden(), testcase.getInput(), testcase.getExpectedOutput(), null, error, false));
                        org.apache.commons.io.FileUtils.deleteQuietly(errorFile);
                    }
                }

            } catch (IOException e) {
                log.error("Testcase [{}]: {}", testcase.getTestcaseId(), e.getLocalizedMessage());
                throw new FileCannotStoreException(e.getLocalizedMessage());
            } catch (InterruptedException e) {
                log.error("Testcase [{}]: {}", testcase.getTestcaseId(), e.getLocalizedMessage());
                submissionResult.getDetails().add(new TestcaseResult(testcase.getHidden(), testcase.getInput(), testcase.getExpectedOutput(), e.getMessage(), false));
            }
        }

        submissionResult.setPassed(passedTestcases);

        return submissionResult;
    }

    @Override
    @Transactional
    public TestcaseVerificationResult verifyTestcase(Long challengeId, TestcaseVerificationRequest payload, UserPrincipal user) {
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

        List<ChallengeConfigEntity> configs = challengeConfigRepository.findAllByChallenge(challenge);

        if (configs.isEmpty()) {
            result.setOtherError("No language config found for this challenge");
            return result;
        }

        for (ChallengeConfigEntity config : configs) {
            LanguageName language = config.getLanguage().getText();
            File challengeDir = new File(storageService.getChallengeDirPathByChallengeAuthorAndConfig(user.getUserId(), config.getChallengeDir()));
            File submissionDir = new File(storageService.getSubmissionDir(user.getUserId(), challenge.getChallengeId().toString()));

            try {
                FileUtils.copyDir2Dir(challengeDir, submissionDir);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
                throw new FileCannotReadException("Cannot copy challenge dir to submission dir");
            }

            String root = submissionDir.getAbsolutePath() + config.getCompilePath().substring(0, config.getCompilePath().lastIndexOf(File.separator));
            try {
                File inputFile = new File(root, FileUtils.INPUT_TESTCASE_FILE);
                FileUtils.overwriteFile(inputFile, payload.getInput());
                CountDownLatch timer = new CountDownLatch(1);
                new Verdict(language, submissionDir.getAbsolutePath(), config.getCompilePath(), config.getRunPath(), timer, false).start();
                timer.await(3000, TimeUnit.MILLISECONDS);

                File errorFile = new File(root, FileUtils.ERROR_FILE);
                if (errorFile.exists()) {
                    String compileError = FileUtils.readFileToString(errorFile);
                    result.getDetails().add(new TestcaseVerificationByLanguage(language.name(), null, null, false, compileError));
                    org.apache.commons.io.FileUtils.deleteQuietly(errorFile);
                    continue;
                }

                File outputFile = new File(root, "out.txt");

                if (outputFile.exists()) {
                    String output = FileUtils.readFileToString(outputFile);
                    if (StringUtils.hasText(output)) {
                        result.getDetails().add(new TestcaseVerificationByLanguage(language.name(), output, null, true, null));
                        org.apache.commons.io.FileUtils.deleteQuietly(outputFile);
                    } else {
                        String error = FileUtils.readFileToString(errorFile);
                        result.getDetails().add(new TestcaseVerificationByLanguage(language.name(), null, error, true, null));
                        org.apache.commons.io.FileUtils.deleteQuietly(errorFile);
                    }
                }

            } catch (IOException e) {
                log.error("Input [{}]: {}", payload.getInput(), e.getLocalizedMessage());
                throw new FileCannotStoreException(e.getLocalizedMessage());
            } catch (InterruptedException e) {
                log.error("Input [{}]: {}", payload.getInput(), e.getLocalizedMessage());
                result.getDetails().add(new TestcaseVerificationByLanguage(language.name(), null, e.getMessage(), true, null));
            }
        }

        return result;
    }

    private String getTestcaseFormat(ChallengeEntity challenge) {
        try {
            return StringUtils.collectionToCommaDelimitedString(RegexUtils.resolveRegex(challenge.getTestcaseInputFormat()));
        } catch (RegexTemplateNotFoundException ignored) {
            return null;
        }
    }
}

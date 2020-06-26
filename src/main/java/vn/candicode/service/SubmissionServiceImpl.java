package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.candicode.core.CodeRunnerService;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;
import vn.candicode.core.StorageService;
import vn.candicode.entity.*;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionDetails;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.ChallengeConfigurationRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.SubmissionRepository;
import vn.candicode.repository.TestcaseRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.FileUtils;
import vn.candicode.util.LanguageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileStorageType.CHALLENGE;
import static vn.candicode.common.FileStorageType.SUBMISSION;

@Service
@Log4j2
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ChallengeConfigurationRepository challengeConfigurationRepository;
    private final TestcaseRepository testcaseRepository;
    private final ChallengeRepository challengeRepository;

    private final StorageService storageService;
    private final CodeRunnerService codeRunnerService;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, ChallengeConfigurationRepository challengeConfigurationRepository, TestcaseRepository testcaseRepository, ChallengeRepository challengeRepository, StorageService storageService, CodeRunnerService codeRunnerService) {
        this.submissionRepository = submissionRepository;
        this.challengeConfigurationRepository = challengeConfigurationRepository;
        this.testcaseRepository = testcaseRepository;
        this.challengeRepository = challengeRepository;
        this.storageService = storageService;
        this.codeRunnerService = codeRunnerService;
    }

    /**
     * @param challengeId
     * @param payload
     * @param me          Only student can do this operation
     * @return
     */
    @Override
    public SubmissionSummary doScoreSubmission(Long challengeId, NewSubmissionRequest payload, UserPrincipal me) {
        Long myId = me.getUserId();
        String language = payload.getLanguage().toLowerCase();

        ChallengeConfigurationEntity configuration = challengeConfigurationRepository
            .findByChallengeIdAndLanguageName(challengeId, language)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeConfigurationEntity.class, "challengeId", challengeId, "languageName", payload.getLanguage()));

        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        List<TestcaseEntity> testcases = challenge.getTestcases();
        int totalTestcases = testcases.size();

        String srcDir = storageService.resolvePath(configuration.getDirectory(), CHALLENGE, configuration.getAuthorId());
        String destDir = storageService.resolvePath(configuration.getDirectory(), SUBMISSION, myId);

        // We will do copy the source to submission folder, so we need to adjust the root dir to reflect it correctly
        String rootDir = storageService.resolvePath(configuration.getRoot(), SUBMISSION, myId);

        CompileResult compileResult;

        List<SubmissionDetails> submissionDetails = new ArrayList<>();

        FileUtils.copyDirectory(new File(srcDir), new File(destDir));
        FileUtils.writeStringToFile(new File(storageService.resolvePath(configuration.getPreImplementedFile(), SUBMISSION, myId)), payload.getCode());

        if (LanguageUtils.requireCompile(language)) {
            compileResult = codeRunnerService.compile(new File(rootDir), language);
        } else {
            compileResult = CompileResult.success(language);
        }

        SubmissionEntity submission = new SubmissionEntity();

        if (!compileResult.isCompiled()) {
            submission.setCompiled(false);
            submission.setDoneWithin(null);
            submission.setExecTime(null);
            submission.setPoint(0);
            submission.setSubmittedCode(payload.getCode());
            submission.setAuthor((StudentEntity) me.getEntityRef());
            submission.setChallenge(challenge);

            submissionRepository.save(submission);

            return SubmissionSummary.builder()
                .compiled("failed")
                .error(compileResult.getCompileError())
                .passed(0)
                .total(totalTestcases)
                .details(new ArrayList<>()).build();
        }

        // Run each test case sequentially
        for (TestcaseEntity testcase : testcases) {
            FileUtils.writeStringToFile(new File(rootDir, "in.txt"), testcase.getInput());
            ExecutionResult executionResult = codeRunnerService.run(new File(rootDir), testcase.getTimeout(), language);
            String error = executionResult.getTimeoutError() != null ? executionResult.getTimeoutError() : executionResult.getRuntimeError();
            submissionDetails.add(SubmissionDetails.builder()
                .testcaseId(testcase.getTestcaseId())
                .input(testcase.getInput())
                .expectedOutput(testcase.getExpectedOutput())
                .actualOutput(executionResult.getOutput())
                .executionTime(executionResult.getExecutionTime())
                .passed(testcase.getExpectedOutput().equals(executionResult.getOutput()))
                .error(error)
                .build()
            );
        }

        Double avgExecutionTime = submissionDetails.stream()
            .filter(item -> item.getExecutionTime() != null)
            .mapToLong(SubmissionDetails::getExecutionTime)
            .average().orElse(Double.NaN);

        int passedTestcases = submissionDetails.stream()
            .filter(item -> Boolean.TRUE.equals(item.getPassed()))
            .mapToInt(item -> 1).sum();

        submission.setCompiled(true);
        submission.setDoneWithin(null);
        submission.setExecTime(avgExecutionTime);
        submission.setPoint(passedTestcases / totalTestcases * challenge.getMaxPoint());
        submission.setSubmittedCode(payload.getCode());
        submission.setAuthor((StudentEntity) me.getEntityRef());
        submission.setChallenge(challenge);

        submissionRepository.save(submission);

        return SubmissionSummary.builder()
            .compiled("success")
            .error(null)
            .total(totalTestcases)
            .passed(passedTestcases)
            .details(submissionDetails)
            .build();
    }

    /**
     * @param pageable
     * @param me
     * @return
     */
    @Override
    public PaginatedResponse<SubmissionSummary> getMySubmissionHistory(Pageable pageable, UserPrincipal me) {


        return null;
    }
}

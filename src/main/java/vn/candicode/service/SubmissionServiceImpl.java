package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.core.CodeRunnerService;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;
import vn.candicode.core.StorageService;
import vn.candicode.entity.*;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewCodeRunRequest;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionDetails;
import vn.candicode.payload.response.SubmissionHistory;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.ChallengeConfigurationRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.CodeExecResultRepository;
import vn.candicode.repository.SubmissionRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.DatetimeUtils;
import vn.candicode.util.FileUtils;
import vn.candicode.util.LanguageUtils;
import vn.candicode.util.SubmissionBeanUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileStorageType.CHALLENGE;
import static vn.candicode.common.FileStorageType.SUBMISSION;

@Service
@Log4j2
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ChallengeConfigurationRepository challengeConfigurationRepository;
    private final ChallengeRepository challengeRepository;
    private final CodeExecResultRepository codeExecResultRepository;

    private final StorageService storageService;
    private final CodeRunnerService codeRunnerService;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, ChallengeConfigurationRepository challengeConfigurationRepository, ChallengeRepository challengeRepository, CodeExecResultRepository codeExecResultRepository, StorageService storageService, CodeRunnerService codeRunnerService) {
        this.submissionRepository = submissionRepository;
        this.challengeConfigurationRepository = challengeConfigurationRepository;
        this.challengeRepository = challengeRepository;
        this.codeExecResultRepository = codeExecResultRepository;
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
    @Transactional
    public SubmissionSummary doScoreSubmission(Long challengeId, NewCodeRunRequest payload, UserPrincipal me) {
        Long myId = me.getUserId();
        String language = payload.getLanguage().toLowerCase();
        String submitAt = LocalDateTime.now().format(DatetimeUtils.JSON_DATETIME_FORMAT);

        ChallengeConfigurationEntity configuration = challengeConfigurationRepository
            .findByChallengeIdAndLanguageName(challengeId, language)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeConfigurationEntity.class, "challengeId", challengeId, "languageName", payload.getLanguage()));

        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        List<TestcaseEntity> testcases = challenge.getTestcases();
        int totalTestcases = testcases.size();

        String srcDir = storageService.resolvePath(configuration.getDirectory(), CHALLENGE, configuration.getAuthorId());
        String destDir = storageService.resolvePath(configuration.getDirectory(), SUBMISSION, myId);

        // We will work in staging folder, so we need to adjust the root dir to reflect it correctly
        final String rootDir = configuration.getDirectory().equals(configuration.getRoot()) ?
            storageService.submissionDirFor(myId) + File.separator + configuration.getRoot() :
            storageService.submissionDirFor(myId) + File.separator + configuration.getDirectory() + File.separator + configuration.getRoot();

        CompileResult compileResult;

        List<SubmissionDetails> submissionDetails = new ArrayList<>();

        FileUtils.copyDirectory(new File(srcDir), new File(destDir));

        final String codeFile = configuration.getDirectory().equals(configuration.getRoot()) ?
            storageService.submissionDirFor(myId) + File.separator + configuration.getPreImplementedFile() :
            storageService.submissionDirFor(myId) + File.separator + configuration.getDirectory() + File.separator + configuration.getPreImplementedFile();

        FileUtils.writeStringToFile(new File(codeFile), payload.getCode());

        if (LanguageUtils.requireCompile(language)) {
            compileResult = codeRunnerService.compile(new File(rootDir), language);
        } else {
            compileResult = CompileResult.success(language);
        }

        if (!compileResult.isCompiled()) {
            SubmissionSummary summary = SubmissionSummary.builder()
                .compiled("failed")
                .error(compileResult.getCompileError())
                .passed(0)
                .total(totalTestcases)
                .details(new ArrayList<>())
                .submitAt(submitAt).build();

            CodeExecResultEntity codeExecResult = new CodeExecResultEntity();
            codeExecResult.setCompositeId(new CodeExecResultId(configuration, me.getEntityRef(), submitAt));
            codeExecResult.setCompiled(false);
            codeExecResult.setDoneWithin(payload.getDoneWithin());
            codeExecResult.setExecTime(0.0);
            codeExecResult.setPassedTestcases(0);
            codeExecResult.setTotalTestcases(totalTestcases);
            codeExecResult.setPoint(0);

            codeExecResultRepository.save(codeExecResult);

            return summary;
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

        CodeExecResultEntity codeExecResult = new CodeExecResultEntity();
        codeExecResult.setCompositeId(new CodeExecResultId(configuration, me.getEntityRef(), submitAt));
        codeExecResult.setCompiled(true);
        codeExecResult.setDoneWithin(payload.getDoneWithin());
        codeExecResult.setExecTime(avgExecutionTime);
        codeExecResult.setPassedTestcases(passedTestcases);
        codeExecResult.setTotalTestcases(totalTestcases);
        codeExecResult.setPoint(passedTestcases / totalTestcases * challenge.getMaxPoint());

        codeExecResultRepository.save(codeExecResult);

        codeRunnerService.cleanGarbageFiles(new File(rootDir), language);

        return SubmissionSummary.builder()
            .compiled("success")
            .error(null)
            .total(totalTestcases)
            .passed(passedTestcases)
            .details(submissionDetails)
            .submitAt(submitAt)
            .build();
    }

    @Transactional
    @Override
    public void saveSubmission(Long challengeId, NewSubmissionRequest payload, UserPrincipal me) {
        ChallengeConfigurationEntity configuration = challengeConfigurationRepository.findByChallengeIdAndLanguageName(challengeId, payload.getLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeConfigurationEntity.class, "challengeId", challengeId, "language", payload.getLanguage()));

        CodeExecResultEntity submissionResult = codeExecResultRepository.findById(new CodeExecResultId(configuration, me.getEntityRef(), payload.getSubmitAt()))
            .orElseThrow(() -> new BadRequestException("Failed to validate your submission"));

        if (!validateSubmission(submissionResult, payload)) {
            throw new BadRequestException("Failed to validate your submission");
        }

        ChallengeEntity challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        SubmissionEntity submission = new SubmissionEntity();

        submission.setCompiled(payload.getCompiled());
        submission.setDoneWithin(payload.getDoneWithin());
        submission.setExecTime(payload.getExecutionTime());
        submission.setPoint(payload.getPassed() / payload.getTotal() * challenge.getMaxPoint());
        submission.setSubmittedCode(payload.getCode());
        submission.setAuthor((StudentEntity) me.getEntityRef());
        submission.setChallenge(challenge);
        submission.setPassedTestcases(payload.getPassed());
        submission.setTotalTestcases(payload.getTotal());
        submission.setDoneWithin(payload.getDoneWithin());
        submission.setAuthorName(me.getFullName());
        submission.setSubmitAt(payload.getSubmitAt());

        submissionRepository.save(submission);
    }

    private boolean validateSubmission(CodeExecResultEntity submissionResult, NewSubmissionRequest submissionRequest) {
        return submissionResult.getCompiled().equals(submissionRequest.getCompiled()) &&
            submissionResult.getDoneWithin().equals(submissionRequest.getDoneWithin()) &&
            submissionResult.getExecTime().equals(submissionRequest.getExecutionTime()) &&
            submissionResult.getPassedTestcases().equals(submissionRequest.getPassed()) &&
            submissionResult.getTotalTestcases().equals(submissionRequest.getTotal());
    }

    /**
     * @param pageable
     * @param me
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SubmissionHistory> getMySubmissionHistory(Pageable pageable, UserPrincipal me) {
        Page<SubmissionEntity> items = submissionRepository.findAllMySubmissions(me.getUserId(), pageable);

        List<SubmissionHistory> summaries = items.map(SubmissionBeanUtils::summarize).getContent();

        return PaginatedResponse.<SubmissionHistory>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SubmissionHistory> getSubmissionsByChallenge(Pageable pageable, Long challengeId) {
        Page<SubmissionEntity> items = submissionRepository.findAllByChallengeId(challengeId, pageable);

        List<SubmissionHistory> summaries = items.map(SubmissionBeanUtils::summarize).getContent();

        return PaginatedResponse.<SubmissionHistory>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<SubmissionHistory> getSubmissionsByContestRound(Pageable pageable, Long roundId) {
        Page<SubmissionEntity> items = submissionRepository.findAllByContestRoundId(roundId, pageable);

        List<SubmissionHistory> summaries = items.map(SubmissionBeanUtils::summarize).getContent();

        return PaginatedResponse.<SubmissionHistory>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }
}

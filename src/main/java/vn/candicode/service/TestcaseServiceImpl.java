package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.core.CodeRunnerService;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ChallengeConfigurationEntity;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.TestcaseEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewTestcaseListRequest;
import vn.candicode.payload.request.TestcaseRequest;
import vn.candicode.payload.request.UpdateTestcaseListRequest;
import vn.candicode.payload.request.VerificationRequest;
import vn.candicode.payload.response.VerificationDetails;
import vn.candicode.payload.response.VerificationSummary;
import vn.candicode.repository.ChallengeConfigurationRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.TestcaseRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.FileUtils;
import vn.candicode.util.RegexUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static vn.candicode.common.FileStorageType.CHALLENGE;
import static vn.candicode.common.FileStorageType.SUBMISSION;

@Service
@Log4j2
public class TestcaseServiceImpl implements TestcaseService {
    private final TestcaseRepository testcaseRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeConfigurationRepository challengeConfigurationRepository;

    private final CodeRunnerService codeRunnerService;
    private final StorageService storageService;

    public TestcaseServiceImpl(TestcaseRepository testcaseRepository, ChallengeRepository challengeRepository, ChallengeConfigurationRepository challengeConfigurationRepository, CodeRunnerService codeRunnerService, StorageService storageService) {
        this.testcaseRepository = testcaseRepository;
        this.challengeRepository = challengeRepository;
        this.challengeConfigurationRepository = challengeConfigurationRepository;
        this.codeRunnerService = codeRunnerService;
        this.storageService = storageService;
    }

    /**
     * @param challengeId
     * @param payload
     * @param currentUser Only challenge's owner can add testcase(s)
     * @return number of successfully added testcases
     */
    @Override
    @Transactional
    public Integer createTestcases(Long challengeId, NewTestcaseListRequest payload, UserPrincipal currentUser) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!challenge.getAuthor().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        Pattern inputFormat = Pattern.compile(challenge.getInputFormat());
        Pattern outputFormat = Pattern.compile(challenge.getOutputFormat());

        int addedTestcases = 0;

        List<TestcaseRequest> testcases = payload.getTestcases();
        for (TestcaseRequest testcase : testcases) {
            if (inputFormat.matcher(testcase.getInput()).matches() && outputFormat.matcher(testcase.getOutput()).matches()) {
                challenge.addTestcase(new TestcaseEntity(testcase.getInput(), testcase.getOutput(), testcase.getHidden(), testcase.getTimeout()));
                addedTestcases = addedTestcases + 1;
            }
        }

        return addedTestcases;
    }

    /**
     * IMPROVEMENTS: Clean verification folders after executing
     *
     * @param challengeId
     * @param payload
     * @param currentUser Only challenge's owner can call to verify testcase on his challenge
     * @return verification result on multiple supported languages
     */
    @Override
    public VerificationSummary verifyTestcase(Long challengeId, VerificationRequest payload, UserPrincipal currentUser) {
        List<ChallengeConfigurationEntity> configurations = challengeConfigurationRepository.findAllByChallengeIdFetchLanguage(challengeId);

        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!challenge.getAuthor().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        Pattern testcaseInputFormatValidator = Pattern.compile(challenge.getInputFormat());

        if (!testcaseInputFormatValidator.matcher(payload.getInput()).matches()) {
            return VerificationSummary.builder()
                .validFormat(false)
                .validFormatError("Input should be " + RegexUtils.resolveRegex(challenge.getInputFormat()))
                .details(null)
                .build();
        }

        long userId = currentUser.getUserId();

        List<CompletableFuture<Void>> compileProcesses = new ArrayList<>();
        List<CompletableFuture<Void>> executionProcesses = new ArrayList<>();

        Map<String, String> languageRootMap = new HashMap<>();

        List<CompileResult> compileResults = new ArrayList<>();
        List<ExecutionResult> executionResults = new ArrayList<>();

        List<VerificationDetails> verificationDetails = new ArrayList<>();

        for (ChallengeConfigurationEntity configuration : configurations) {
            String srcDir = storageService.resolvePath(configuration.getDirectory(), CHALLENGE, userId);
            String destDir = storageService.resolvePath(configuration.getDirectory(), SUBMISSION, userId);
            String language = configuration.getLanguage().getName();

            // We have copied the source to submission folder, so we need to adjust the root dir to reflect it correctly
            String rootDir = storageService.resolvePath(configuration.getRoot(), SUBMISSION, userId);

            languageRootMap.put(language, rootDir);

            CompletableFuture<Void> compileProcess = CompletableFuture
                .runAsync(() -> FileUtils.copyDirectory(new File(srcDir), new File(destDir)))
                .thenApply(nil -> rootDir)
                .thenApply(dir -> codeRunnerService.compile(new File(dir), language))
                .thenAccept(compileResults::add);

            compileProcesses.add(compileProcess);
        }

        try {
            CompletableFuture.allOf(compileProcesses.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.error("Compile failed with exception {}. Message - {}", e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        }

        for (CompileResult compileResult : compileResults) {
            if (!compileResult.isCompiled()) {
                verificationDetails.add(VerificationDetails.builder()
                    .compiled(false)
                    .compileError(compileResult.getCompileError())
                    .language(compileResult.getLanguage())
                    .executionTime(0)
                    .timoutError(null)
                    .runtimeError(null)
                    .output(null)
                    .build()
                );
            } else {
                String rootDir = languageRootMap.get(compileResult.getLanguage());
                CompletableFuture<Void> executionProcess = CompletableFuture
                    .runAsync(() -> FileUtils.writeStringToFile(new File(rootDir, "in.txt"), payload.getInput()))
                    .thenApply(nil -> rootDir)
                    .thenApply(dir -> codeRunnerService.run(new File(dir), payload.getTimeout(), compileResult.getLanguage()))
                    .thenAccept(executionResults::add);

                executionProcesses.add(executionProcess);
            }
        }

        try {
            CompletableFuture.allOf(executionProcesses.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.error("Execute failed with exception {}. Message - {}", e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        }

        for (ExecutionResult executionResult : executionResults) {
            verificationDetails.add(VerificationDetails.builder()
                .compiled(true)
                .compileError(null)
                .language(executionResult.getLanguage())
                .executionTime(executionResult.getExecutionTime())
                .timoutError(executionResult.getTimeoutError())
                .runtimeError(executionResult.getRuntimeError())
                .output(executionResult.getOutput())
                .build()
            );
        }

        return VerificationSummary.builder()
            .validFormat(true)
            .validFormatError(null)
            .details(verificationDetails)
            .build();
    }

    /**
     * @param challengeId
     * @param payload
     * @param me          Only challenge's owner can update testcases of his challenge
     * @return number of successfully updated testcases
     */
    @Override
    @Transactional
    public Integer updateTestcases(Long challengeId, UpdateTestcaseListRequest payload, UserPrincipal me) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!challenge.getAuthor().getUserId().equals(me.getUserId())) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        Pattern inputFormat = Pattern.compile(challenge.getInputFormat());
        Pattern outputFormat = Pattern.compile(challenge.getOutputFormat());

        Map<Long, TestcaseRequest> validTestcases = new HashMap<>();

        for (TestcaseRequest req : payload.getTestcases()) {
            if (inputFormat.matcher(req.getInput()).matches() && outputFormat.matcher(req.getOutput()).matches()) {
                validTestcases.put(req.getTestcaseId(), req);
            }
        }

        int updatedTestcases = 0;

        List<TestcaseEntity> testcaseEntities = challenge.getTestcases();
        for (TestcaseEntity testcaseEntity : testcaseEntities) {
            if (validTestcases.containsKey(testcaseEntity.getTestcaseId())) {
                TestcaseRequest req = validTestcases.get(testcaseEntity.getTestcaseId());
                testcaseEntity.setInput(req.getInput());
                testcaseEntity.setExpectedOutput(req.getOutput());
                testcaseEntity.setHidden(req.getHidden());
                updatedTestcases++;
            }
        }

        testcaseRepository.saveAll(testcaseEntities);

        return updatedTestcases;
    }

    /**
     * @param challengeId
     * @param testcaseIds
     * @param me          Only challenge's owner can delete his challenge's testcases
     * @return an array that contains:
     * <ul>
     *     <li>Number of deleted testcases</li>
     *     <li>Number of remaining testcases</li>
     * </ul>
     */
    @Override
    @Transactional
    public Integer[] deleteTestcases(Long challengeId, List<Long> testcaseIds, UserPrincipal me) {
        List<TestcaseEntity> testcaseEntities = testcaseRepository.findAllByChallengeId(challengeId);

        List<TestcaseEntity> removedTestcases = testcaseEntities.stream()
            .filter(t -> testcaseIds.contains(t.getTestcaseId()))
            .collect(Collectors.toList());

        Integer[] testcaseState = new Integer[]{removedTestcases.size(), testcaseEntities.size() - removedTestcases.size()};

        if (removedTestcases.size() > 0) {
            if (!removedTestcases.get(0).getChallenge().getAuthor().getUserId().equals(me.getUserId())) {
                throw new BadRequestException("You are not the owner of this challenge");
            }

            testcaseRepository.deleteAll(removedTestcases);
        }

        return testcaseState;
    }
}

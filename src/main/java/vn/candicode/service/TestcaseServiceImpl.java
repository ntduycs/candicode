package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.FileOperationResult;
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
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static vn.candicode.common.FileOperationResult.SUCCESS;
import static vn.candicode.common.FileStorageType.CHALLENGE;
import static vn.candicode.common.FileStorageType.STAGING;

@Service
@Log4j2
public class TestcaseServiceImpl implements TestcaseService {
    private final ChallengeRepository challengeRepository;

    private final CodeRunnerService codeRunnerService;
    private final StorageService storageService;

    public TestcaseServiceImpl(ChallengeRepository challengeRepository, CodeRunnerService codeRunnerService, StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.codeRunnerService = codeRunnerService;
        this.storageService = storageService;
    }

    /**
     * @param challengeId target challenge's id
     * @param payload     testcase payload
     * @param currentUser Only challenge's owner can add testcase(s)
     * @return number of successfully added testcases
     */
    @Override
    @Transactional
    public Integer createTestcases(Long challengeId, NewTestcaseListRequest payload, UserPrincipal currentUser) {
        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchAuthor(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, currentUser)) {
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

        if (!challenge.getAvailable() && addedTestcases > 0) {
            ChallengeConfigurationEntity configuration = challenge.getConfigurations().get(0);

            configuration.setEnabled(true);

            File srcDir = new File(storageService.resolvePath(configuration.getDirectory(), STAGING, currentUser.getUserId()));
            File challengeDir = new File(storageService.challengeDirFor(currentUser.getUserId()).toString());

            FileOperationResult result = FileUtils.copyDirectoryToDirectory(srcDir, challengeDir);

            if (!result.equals(SUCCESS)) {
                log.error("Error when activating challenge with id {}. Message - {}", challengeId, "Cannot copy src to challenge dir");
            }

            challenge.setAvailable(true);
        }

        return addedTestcases;
    }

    private boolean isMyChallenge(ChallengeEntity challenge, UserPrincipal me) {
        return challenge.getAuthor().getUserId().equals(me.getUserId());
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
        ChallengeEntity challenge = challengeRepository.findByChallengeFetchConfigurationsAndAuthor(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, currentUser)) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        Pattern testcaseInputFormatValidator = Pattern.compile(challenge.getInputFormat());

        if (!testcaseInputFormatValidator.matcher(payload.getInput()).matches()) {
            return VerificationSummary.invalidTestcaseFormat(challenge.getInputFormat());
        }

        long userId = currentUser.getUserId();

        List<VerificationDetails> verificationDetails = new ArrayList<>();

        Map<String, String> languageRootMap = new HashMap<>();

        // ==========================================================
        // = Compilation phase =
        // ==========================================================
        List<CompletableFuture<Void>> compileProcesses = new ArrayList<>();
        List<CompileResult> compileResults = new ArrayList<>();
        for (ChallengeConfigurationEntity configuration : challenge.getConfigurations()) {
            String language = configuration.getLanguage().getName();

            // We will work in staging folder, so we need to adjust the root dir to reflect it correctly
            final String rootDir = configuration.getDirectory().equals(configuration.getRoot()) ?
                storageService.stagingDirFor(userId) + File.separator + configuration.getRoot() :
                storageService.stagingDirFor(userId) + File.separator + configuration.getDirectory() + File.separator + configuration.getRoot();

            languageRootMap.put(language, rootDir);

            CompletableFuture<Void> compileProcess;

            if (!challenge.getAvailable() || !configuration.getEnabled()) {
                compileProcess = CompletableFuture
                    .supplyAsync(() -> codeRunnerService.compile(new File(rootDir), language))
                    .thenAcceptAsync(compileResults::add);
            } else {
                String srcDir = storageService.resolvePath(configuration.getDirectory(), CHALLENGE, userId);
                String destDir = storageService.resolvePath(configuration.getDirectory(), STAGING, userId);

                compileProcess = CompletableFuture
                    .runAsync(() -> FileUtils.copyDirectoryToDirectory(new File(srcDir), new File(destDir)))
                    .thenApplyAsync(nil -> codeRunnerService.compile(new File(rootDir), language))
                    .thenAcceptAsync(compileResults::add);
            }

            compileProcesses.add(compileProcess);
        }

        try {
            CompletableFuture.allOf(compileProcesses.toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.error("Compile failed with exception {}. Message - {}", e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        }

        // ==========================================================
        // = Execution phase =
        // ==========================================================
        List<CompletableFuture<Void>> executionProcesses = new ArrayList<>();
        List<ExecutionResult> executionResults = new ArrayList<>();
        for (CompileResult compileResult : compileResults) {
            if (!compileResult.isCompiled()) {
                verificationDetails.add(VerificationDetails.compileFailed(compileResult));
            } else {
                String rootDir = languageRootMap.get(compileResult.getLanguage());
                CompletableFuture<Void> executionProcess = CompletableFuture
                    .runAsync(() -> FileUtils.writeStringToFile(new File(rootDir, "in.txt"), payload.getInput()))
                    .thenApply(nil -> codeRunnerService.run(new File(rootDir), payload.getTimeout(), compileResult.getLanguage()))
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
            verificationDetails.add(VerificationDetails.executeCompleted(executionResult));
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
        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchAuthorAndTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, me)) {
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
                testcaseEntity.setTimeout(req.getTimeout());
                updatedTestcases++;
            }
        }

        challengeRepository.save(challenge);

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
        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchAuthorAndTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, me)) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        List<TestcaseEntity> testcaseEntities = challenge.getTestcases();

        int currentTestcases = testcaseEntities.size();

        List<TestcaseEntity> removedTestcases = testcaseEntities.stream()
            .filter(t -> testcaseIds.contains(t.getTestcaseId()))
            .collect(Collectors.toList());

        if (removedTestcases.size() > 0) {
            challenge.removeTestcases(removedTestcases);
        }

        return new Integer[]{removedTestcases.size(), currentTestcases - removedTestcases.size()};
    }
}

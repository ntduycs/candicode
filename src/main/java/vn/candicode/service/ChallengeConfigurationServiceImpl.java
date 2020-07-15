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
import vn.candicode.entity.LanguageEntity;
import vn.candicode.entity.TestcaseEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewChallengeConfigurationRequest;
import vn.candicode.payload.response.SubmissionDetails;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.ChallengeConfigurationRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.LanguageRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.FileUtils;
import vn.candicode.util.LanguageUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileOperationResult.SUCCESS;
import static vn.candicode.common.FileStorageType.STAGING;

@Service
@Log4j2
public class ChallengeConfigurationServiceImpl implements ChallengeConfigurationService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeConfigurationRepository challengeConfigurationRepository;

    private final StorageService storageService;
    private final CodeRunnerService codeRunnerService;
    private final CommonService commonService;

    public ChallengeConfigurationServiceImpl(ChallengeRepository challengeRepository, ChallengeConfigurationRepository challengeConfigurationRepository, LanguageRepository languageRepository, StorageService storageService, CodeRunnerService codeRunnerService, CommonService commonService) {
        this.challengeRepository = challengeRepository;
        this.challengeConfigurationRepository = challengeConfigurationRepository;

        this.storageService = storageService;
        this.codeRunnerService = codeRunnerService;
        this.commonService = commonService;
    }

    /**
     * Execute the pre-implemented code and do evaluation on existing testcases.
     * If succeed to passed entirely, store this supported language to DB.
     * Otherwise, remove all files related to this operation.
     *
     * <p>Only challenge's owner can do this operation</p>
     *
     * @param challengeId
     * @param payload
     * @param me
     * @return
     */
    @Override
    @Transactional
    public SubmissionSummary addSupportedLanguage(Long challengeId, NewChallengeConfigurationRequest payload, UserPrincipal me) {
        Long myId = me.getUserId();
        String language = payload.getLanguage().toLowerCase();

        if (!commonService.getLanguages().containsKey(language)) {
            throw new ResourceNotFoundException(LanguageEntity.class, "name", payload.getLanguage());
        }

        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchAuthorAndTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, me)) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        List<TestcaseEntity> testcases = challenge.getTestcases();
        int totalTestcases = testcases.size();

        String rootRelativePath = payload.getRunPath().startsWith(File.separator) ?
            payload.getRunPath().substring(1, payload.getRunPath().lastIndexOf(File.separator)) :
            payload.getRunPath().substring(0, payload.getRunPath().lastIndexOf(File.separator));

        String rootDir = storageService.resolvePath(payload.getChallengeDir(), STAGING, myId) + File.separator + rootRelativePath;

        CompileResult compileResult;

        List<SubmissionDetails> submissionDetails = new ArrayList<>();

        if (LanguageUtils.requireCompile(language)) {
            compileResult = codeRunnerService.compile(new File(rootDir), language);
        } else {
            compileResult = CompileResult.success(language);
        }

        if (!compileResult.isCompiled()) {
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

        int passedTestcases = submissionDetails.stream().filter(SubmissionDetails::getPassed).mapToInt(item -> 1).sum();

        if (passedTestcases == totalTestcases) {
            ChallengeConfigurationEntity challengeConfig = new ChallengeConfigurationEntity();

            challengeConfig.setLanguage(commonService.getLanguages().get(language));
            challengeConfig.setDirectory(payload.getChallengeDir());
            challengeConfig.setRoot(Paths.get(payload.getRunPath()).getParent().toString().substring(1));
            challengeConfig.setPreImplementedFile(payload.getImplementedPath().substring(1));
            challengeConfig.setNonImplementedFile(payload.getNonImplementedPath().substring(1));
            challengeConfig.setRunScript(payload.getRunPath().substring(1));
            if (payload.getCompilePath() != null) {
                challengeConfig.setCompileScript(payload.getCompilePath().substring(1));
            }
            challengeConfig.setAuthorId(myId);
            challengeConfig.setEnabled(true);

            challenge.addConfiguration(challengeConfig);

            File srcDir = new File(storageService.resolvePath(challengeConfig.getDirectory(), STAGING, myId));
            File challengeDir = new File(storageService.challengeDirFor(myId).toString());

            FileOperationResult result = FileUtils.copyDirectoryToDirectory(srcDir, challengeDir);

            if (!result.equals(SUCCESS)) {
                log.error("Error when activating challenge with id {}. Message - {}", challengeId, "Cannot copy src to challenge dir");
            }
        }

        return SubmissionSummary.builder()
            .compiled("success")
            .error(null)
            .total(totalTestcases)
            .passed(passedTestcases)
            .details(submissionDetails)
            .build();
    }

    private boolean isMyChallenge(ChallengeEntity challenge, UserPrincipal me) {
        return challenge.getAuthor().getUserId().equals(me.getUserId());
    }

    private boolean isMyChallenge(ChallengeConfigurationEntity configuration, UserPrincipal me) {
        return configuration.getAuthorId().equals(me.getUserId());
    }

    /**
     * Softly delete db record
     *
     * @param challengeId
     * @param language    which language that want/need to remove
     * @return true if removed successfully
     */
    @Override
    @Transactional
    public Boolean removeSupportedLanguage(Long challengeId, String language, UserPrincipal me) {
        ChallengeConfigurationEntity configuration = challengeConfigurationRepository
            .findByChallengeIdAndLanguageName(challengeId, language.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeConfigurationEntity.class, "challengeId", challengeId, "languageName", language));

        if (!isMyChallenge(configuration, me)) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        configuration.setDeleted(true);

        if (configuration.getChallenge().getConfigurations().stream().noneMatch(cf -> cf.getDeleted() || cf.getEnabled())) {
            configuration.getChallenge().setAvailable(false);
        }

        return true;
    }
}

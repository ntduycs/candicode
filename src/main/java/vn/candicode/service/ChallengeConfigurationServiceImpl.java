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
import vn.candicode.entity.LanguageEntity;
import vn.candicode.entity.TestcaseEntity;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewChallengeConfigurationRequest;
import vn.candicode.payload.response.SubmissionDetails;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.ChallengeConfigurationRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.security.LanguageRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.FileUtils;
import vn.candicode.util.LanguageUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileStorageType.CHALLENGE;
import static vn.candicode.common.FileStorageType.SUBMISSION;

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
    public SubmissionSummary addSupportedLanguage(Long challengeId, NewChallengeConfigurationRequest payload, UserPrincipal me) {
        Long myId = me.getUserId();
        String language = payload.getLanguage().toLowerCase();

        if (!commonService.getLanguages().containsKey(language)) {
            throw new ResourceNotFoundException(LanguageEntity.class, "name", payload.getLanguage());
        }

        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchTestcases(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        List<TestcaseEntity> testcases = challenge.getTestcases();
        int totalTestcases = testcases.size();

        String srcDir = storageService.resolvePath(payload.getChallengeDir(), CHALLENGE, myId);
        String destDir = storageService.resolvePath(payload.getChallengeDir(), SUBMISSION, myId);

        // We will do copy the source to submission folder, so we need to adjust the root dir to reflect it correctly
        String rootDir = Paths.get(payload.getRunPath()).getParent().toString().replaceFirst("challenges", "submissions");

        CompileResult compileResult;

        List<SubmissionDetails> submissionDetails = new ArrayList<>();

        FileUtils.copyDirectory(new File(srcDir), new File(destDir));

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

            challengeConfig.setChallenge(challenge);
            challengeConfig.setLanguage(commonService.getLanguages().get(language));
            challengeConfig.setDirectory(payload.getChallengeDir());
            challengeConfig.setRoot(rootDir);
            challengeConfig.setPreImplementedFile(storageService.simplifyPath(payload.getImplementedPath(), CHALLENGE, myId));
            challengeConfig.setNonImplementedFile(storageService.simplifyPath(payload.getNonImplementedPath(), CHALLENGE, myId));
            challengeConfig.setRunScript(storageService.simplifyPath(payload.getRunPath(), CHALLENGE, myId));
            challengeConfig.setCompileScript(storageService.simplifyPath(payload.getCompilePath(), CHALLENGE, myId));
            challengeConfig.setAuthorId(myId);

            challengeConfigurationRepository.save(challengeConfig);
        }

        return SubmissionSummary.builder()
            .compiled("success")
            .error(null)
            .total(totalTestcases)
            .passed(passedTestcases)
            .details(submissionDetails)
            .build();
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

        configuration.setDeleted(true);

        return true;
    }
}

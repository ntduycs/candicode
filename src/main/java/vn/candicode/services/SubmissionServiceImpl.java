package vn.candicode.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.exceptions.ResourceNotFoundException;
import vn.candicode.exceptions.StorageException;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeConfig;
import vn.candicode.models.ChallengeTestcase;
import vn.candicode.models.User;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.responses.SubmissionResult;
import vn.candicode.repositories.ChallengeConfigRepository;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.SubmissionRepository;
import vn.candicode.repositories.TestcaseRepository;
import vn.candicode.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ChallengeRepository challengeRepository;
    private final TestcaseRepository testcaseRepository;
    private final ChallengeConfigRepository challengeConfigRepository;

    private final InMemoryService inMemoryService;
    private final StorageService storageService;

    private final StorageLocation storageLocation;

    private final FileUtils fileUtils;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 ChallengeRepository challengeRepository,
                                 TestcaseRepository testcaseRepository,
                                 ChallengeConfigRepository challengeConfigRepository,
                                 InMemoryService inMemoryService,
                                 StorageService storageService,
                                 StorageLocation storageLocation,
                                 FileUtils fileUtils) {
        this.submissionRepository = submissionRepository;
        this.challengeRepository = challengeRepository;
        this.testcaseRepository = testcaseRepository;
        this.challengeConfigRepository = challengeConfigRepository;
        this.inMemoryService = inMemoryService;
        this.storageService = storageService;
        this.storageLocation = storageLocation;
        this.fileUtils = fileUtils;
    }

    @Override
    public SubmissionResult check(SubmissionRequest request, User user) {
        Challenge challenge = challengeRepository.findByIdAndDeletedAtIsNull(request.getChallengeId())
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", request.getChallengeId()));

        List<ChallengeTestcase> testcases = testcaseRepository.findAllByChallengeAndDeletedAtIsNull(challenge);

        vn.candicode.models.ChallengeLanguage language =
            inMemoryService.challengeLanguages().get(ChallengeLanguage.valueOf(request.getCodeLanguage().toUpperCase()));

        ChallengeConfig challengeConfig = challengeConfigRepository.findByChallengeAndLanguage(challenge, language)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge Config", "{challengeId and language}", String.format("%s and %s", request.getChallengeId(), request.getCodeLanguage())));

        String challengeSourceDir = storageLocation.getChallengeStorageLocationByUser(user.getId()) + File.separator + challengeConfig.getChallengeDir();

        String challengeSubmissionDir = storageLocation.getSubmissionStorageLocationByUser(user.getId()).toString() + File.separator + challengeConfig.getChallengeDir();

        try {
            fileUtils.copyDirectory(challengeSourceDir, challengeSubmissionDir);
        } catch (IOException e) {
            log.error("\n\nError when copying source code to submission folder. Message - {}", e.getMessage());
            throw new StorageException("Error when copying source code to submission folder.");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(challengeConfig.getNonImplementedPath().replaceFirst("challenges", "submissions")))

        return null;
    }
}

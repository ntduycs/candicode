package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.adapter.AntdAdapter;
import vn.candicode.exceptions.FileCannotStoreException;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.responses.SourceCodeStructure;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.LanguageRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;
import vn.candicode.services.StorageService;
import vn.candicode.utils.PreloadEntities;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;

import static vn.candicode.services.StorageService.Factor.CHALLENGE;

@Service
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final StorageService storageService;

    private final ChallengeRepository challengeRepository;
    private final LanguageRepository languageRepository;

    @Autowired
    private PreloadEntities preloadEntities;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeServiceImpl(StorageService storageService, ChallengeRepository challengeRepository, LanguageRepository languageRepository) {
        this.storageService = storageService;
        this.challengeRepository = challengeRepository;
        this.languageRepository = languageRepository;
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
            challenge.setTestcaseInputFormat(payload.getTcInputFormat());
            challenge.setTestcaseOutputFormat(payload.getTcOutputFormat());
            challenge.setLevel(ChallengeLevel.valueOf(payload.getLevel().toUpperCase()));
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
            case HARD:
                return 300;
            case MODERATE:
                return 200;
            case EASY:
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

}

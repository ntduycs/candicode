package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ChallengeConfigurationEntity;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.LanguageEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.StorageException;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.request.UpdateChallengeRequest;
import vn.candicode.payload.response.ChallengeDetails;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.security.LanguageRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.RegexUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.candicode.common.FileStorageType.BANNER;
import static vn.candicode.common.FileStorageType.CHALLENGE;

@Service
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;

    private final StorageService storageService;

    private final Map<String, LanguageEntity> availableLanguages;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, LanguageRepository languageRepository, StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.storageService = storageService;

        this.availableLanguages = languageRepository.findAll().stream().collect(Collectors.toMap(LanguageEntity::getName, lang -> lang));
    }

    /**
     * @param payload
     * @param author
     * @return id of new challenge
     */
    @Override
    @Transactional
    public Long createChallenge(NewChallengeRequest payload, UserPrincipal author) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.store(payload.getBanner(), BANNER, author.getUserId());
            }

            ChallengeEntity challenge = new ChallengeEntity();

            challenge.setTitle(payload.getTitle());
            challenge.setDescription(payload.getDescription());
            challenge.setInputFormat(RegexUtils.genRegex(payload.getTcInputFormat()));
            challenge.setOutputFormat(RegexUtils.genRegex(payload.getTcOutputFormat()));
            challenge.setLevel(payload.getLevel());
            challenge.setMaxPoint(challenge.getLevel());
            challenge.setAuthor(author.getEntityRef());
            challenge.setBanner(bannerPath);
            challenge.setTags(payload.getTags());
            challenge.setContestChallenge(payload.getContestChallenge());

            entityManager.persist(challenge);

            ChallengeConfigurationEntity challengeConfig = new ChallengeConfigurationEntity();

            challengeConfig.setChallenge(challenge);
            challengeConfig.setLanguage(availableLanguages.get(payload.getLanguage()));
            challengeConfig.setDirectory(payload.getChallengeDir());
            challengeConfig.setPreImplementedFile(payload.getImplementedPath());
            challengeConfig.setNonImplementedFile(payload.getNonImplementedPath());
            challengeConfig.setRunScript(payload.getRunPath());

            /*
             * Root dir is the folder that the run script is placed in
             * */
            String rootDir = Paths.get(payload.getRunPath()).getParent().toString();
            challengeConfig.setRoot(rootDir);

            if (payload.getCompilePath() != null) {
                challengeConfig.setCompileScript(payload.getCompilePath());
            }

            entityManager.persist(challengeConfig);

            return challenge.getChallengeId();
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        } catch (EntityExistsException e) {
            log.error("Entity has already existing. Message - {}", e.getLocalizedMessage());
            throw new PersistenceException(e.getLocalizedMessage());
        }
    }

    /**
     * @param file   must be a zip file
     * @param author
     * @return
     */
    @Override
    public DirectoryTree storeChallengeSource(MultipartFile file, UserPrincipal author) {
        try {
            String challengeDir = storageService.store(file, CHALLENGE, author.getUserId());
            String challengeDirname = challengeDir.substring(challengeDir.lastIndexOf(File.separator));

            DirectoryTree tree = new DirectoryTree();
            tree.setChallengeDir(challengeDirname);
            tree.setChildren(storageService.parse(challengeDir));

            return tree;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        }
    }

    /**
     * TODO: Implement it
     *
     * @param pageable
     * @return paginated list of challenges
     */
    @Override
    public PaginatedResponse<ChallengeSummary> getChallengeList(Pageable pageable) {
        Page<ChallengeEntity> items = challengeRepository.findAll(pageable);

        return null;
    }

    /**
     * @param pageable
     * @param myId
     * @return paginated list of my challenges
     */
    @Override
    public PaginatedResponse<ChallengeSummary> getMyChallengeList(Pageable pageable, Long myId) {
        return null;
    }

    /**
     * @param challengeId
     * @return details of challenge with given id
     */
    @Override
    public ChallengeDetails getChallengeDetails(Long challengeId) {
        return null;
    }

    /**
     * Only author can edit challenge
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     */
    @Override
    public void updateChallenge(Long challengeId, UpdateChallengeRequest payload, UserPrincipal currentUser) {

    }

    /**
     * <ul>
     *     <li>Only author can delete his challenge</li>
     *     <li>Call this method will delete both DB records and related filesystem directories</li>
     * </ul>
     *  @param challengeId
     *
     * @param currentUser
     */
    @Override
    public void deleteChallenge(Long challengeId, UserPrincipal currentUser) {

    }
}

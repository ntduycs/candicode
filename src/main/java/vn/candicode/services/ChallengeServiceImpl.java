package vn.candicode.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.exceptions.StorageException;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeConfig;
import vn.candicode.models.User;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.utils.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository repository;

    private final InMemoryService inMemoryService;

    private final StorageLocation storageLocation;

    private final FileUtils fileUtils;

    @PersistenceContext
    EntityManager em;

    public ChallengeServiceImpl(ChallengeRepository repository, InMemoryService inMemoryService, StorageLocation storageLocation, FileUtils fileUtils) {
        this.repository = repository;
        this.inMemoryService = inMemoryService;
        this.storageLocation = storageLocation;
        this.fileUtils = fileUtils;
    }

    @Override
    @Transactional
    public Long createChallenge(ChallengeRequest request, User user) {
        ChallengeLevel level = ChallengeLevel.valueOf(request.getLevel().toUpperCase());

        int point = getPointByLevel(level);

        try {
            Map<String, String> bannerAndDescPaths = storeFiles(request, user);

            Challenge challenge = new Challenge(
                request.getTitle(),
                level,
                bannerAndDescPaths.get("description"),
                point,
                request.getTcInputFormat(),
                request.getTcOutputFormat()
            );

            challenge.setBannerPath(bannerAndDescPaths.get("banner"));

            em.persist(challenge);

            ChallengeConfig config = new ChallengeConfig(
                challenge,
                inMemoryService.challengeLanguages().get(ChallengeLanguage.valueOf(request.getLanguage().toUpperCase())),
                request.getTargetPath(),
                request.getBuildPath()
            );

            repository.findById(challenge.getId()).ifPresentOrElse(c->c.addConfig(config),() -> {
                throw new StorageException("Failed to store challenge");
            });

            return repository.save(challenge).getId();
        } catch (IOException e) {
            throw new StorageException(e.getLocalizedMessage());
        }
    }

    private Map<String, String> storeFiles(ChallengeRequest request, User user) throws IOException {
        Map<String, String> paths = new HashMap<>();

        Path userStorage = storageLocation.getChallengeStorageLocationByUser(user.getId());

        if (request.getBanner() != null) {
            MultipartFile banner = request.getBanner();
            banner.transferTo(new File(userStorage + File.separator + banner.getOriginalFilename()));
            paths.put("banner", userStorage + File.separator + banner.getOriginalFilename());
        }

        if (request.getDescription() != null) {
            MultipartFile description = request.getDescription();
            description.transferTo(new File(userStorage + File.separator + description.getOriginalFilename()));
            paths.put("description", userStorage + File.separator + description.getOriginalFilename());
        }

        return paths;
    }

    private int getPointByLevel(ChallengeLevel level) {
        switch (level) {
            case EASY:
                return 100;
            case MODERATE:
                return 200;
            case HARD:
                return 300;
            default:
                throw new IllegalArgumentException("Challenge level not found");
        }
    }

    @Override
    public Component parseDirTree(MultipartFile sourceCode, User user) {
        return fileUtils.parseDirTree(sourceCode, user);
    }
}

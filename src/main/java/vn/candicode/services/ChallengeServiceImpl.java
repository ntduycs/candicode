package vn.candicode.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.exceptions.ResourceNotFoundException;
import vn.candicode.exceptions.StorageException;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeConfig;
import vn.candicode.models.User;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeContent;
import vn.candicode.payloads.responses.ChallengeDetail;
import vn.candicode.payloads.responses.ChallengeSummary;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.utils.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
            String bannerPath = null;

            if (request.getBanner() != null && !request.getBanner().isEmpty()) {
                bannerPath = storeFiles(request.getBanner(), user);
            }

            Challenge challenge = new Challenge(
                request.getTitle(),
                level,
                request.getDescription(),
                point,
                request.getTcInputFormat(),
                request.getTcOutputFormat()
            );

            challenge.setBannerPath(bannerPath);

            em.persist(challenge);

            ChallengeConfig config = new ChallengeConfig(
                challenge,
                inMemoryService.challengeLanguages().get(ChallengeLanguage.valueOf(request.getLanguage().toUpperCase())),
                request.getTargetPath(),
                request.getBuildPath(),
                request.getEditPath()
            );

            repository.findById(challenge.getId()).ifPresentOrElse(c -> c.addConfig(config), () -> {
                throw new StorageException("Failed to store challenge");
            });

            return repository.save(challenge).getId();
        } catch (IOException e) {
            throw new StorageException(e.getLocalizedMessage());
        }
    }

    private String storeFiles(MultipartFile banner, User user) throws IOException {
        Path userStorage = storageLocation.getChallengeStorageLocationByUser(user.getId());

        if (banner != null) {
            banner.transferTo(new File(userStorage + File.separator + banner.getOriginalFilename()));
            return userStorage + File.separator + banner.getOriginalFilename();
        }

        return null;
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

    @Override
    @Transactional(readOnly = true)
    public ChallengeDetail getChallengeById(Long id) {
        Challenge challenge = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", id));

        List<ChallengeContent> contents = new ArrayList<>();

        challenge.getConfigurations().forEach(config -> {
            try {
                contents.add(new ChallengeContent(config.getLanguage().getName().name(), fileUtils.readFile(config.getTargetPath())));
            } catch (IOException e) {
                throw new StorageException("Cannot read file");
            }
        });

        return new ChallengeDetail(
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getBannerPath(),
            challenge.getLevel().name(),
            challenge.getPoints(),
            challenge.getTestcaseInputFormat(),
            challenge.getTestcaseOutputFormat(),
            contents
        );

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getChallenges(Pageable pageable) {
        Map<String, Object> ret = new LinkedHashMap<>();

        Page<Challenge> challenges = repository.findAll(pageable);

        List<ChallengeSummary> items = challenges.getContent().stream().map(challenge -> new ChallengeSummary(
            challenge.getId(),
            challenge.getTitle(),
            challenge.getBannerPath(),
            challenge.getDescription(),
            challenge.getLevel().name(),
            challenge.getPoints(),
            challenge.getConfigurations().stream().map(config -> config.getLanguage().getName().name()).collect(Collectors.toList()),
            0L
        )).collect(Collectors.toList());

        ret.put("page", challenges.getNumber());
        ret.put("size", challenges.getSize());
        ret.put("totalElements", challenges.getTotalElements());
        ret.put("totalPages", challenges.getTotalPages());
        ret.put("first", challenges.isFirst());
        ret.put("last", challenges.isLast());
        ret.put("items", items);

        return ret;
    }
}

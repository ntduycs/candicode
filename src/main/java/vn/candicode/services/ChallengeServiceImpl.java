package vn.candicode.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.exceptions.*;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeConfig;
import vn.candicode.models.ChallengeTestcase;
import vn.candicode.models.User;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.models.enums.UserType;
import vn.candicode.payloads.requests.*;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
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
                user,
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
                request.getRunPath(),
                request.getCompilePath(),
                request.getImplementedPath(),
                request.getNonImplementedPath(),
                request.getChallengeDir()
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
    public Map<String, Object> parseDirTree(MultipartFile sourceCode, User user) {
        return fileUtils.parseDirTree(sourceCode, user);
    }

    @Override
    @Transactional(readOnly = true)
    public ChallengeDetail getChallengeById(Long id) {
        Challenge challenge = repository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", id));

        List<ChallengeContent> contents = new ArrayList<>();

        challenge.getConfigurations().forEach(config -> {
            try {
                contents.add(new ChallengeContent(config.getLanguage().getName().name(), fileUtils.readFile(config.getNonImplementedPath())));
            } catch (IOException e) {
                throw new StorageException("Cannot read file");
            }
        });

        List<vn.candicode.payloads.responses.Testcase> testcases = challenge.getTestcases().stream().map(testcase -> new vn.candicode.payloads.responses.Testcase(
            testcase.getInput(),
            testcase.isPublicTestcase() ? testcase.getExpectedOutput() : null,
            testcase.isPublicTestcase()
        )).collect(Collectors.toList());

        ChallengeDetail challengeDetail = new ChallengeDetail(
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getLevel().name(),
            challenge.getPoints(),
            challenge.getTestcaseInputFormat(),
            challenge.getTestcaseOutputFormat(),
            contents,
            testcases
        );

        challengeDetail.setBanner(challenge.getBannerPath());

        return challengeDetail;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getChallenges(Pageable pageable) {
        Map<String, Object> ret = new LinkedHashMap<>();

        Page<Challenge> challenges = repository.findAllByDeletedAtIsNull(pageable);

        return wrapChallenges2Container(ret, challenges);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMyChallenges(Pageable pageable, User user) {
        Map<String, Object> ret = new LinkedHashMap<>();

        Page<Challenge> challenges = repository.findAllByCreatedByAndDeletedAtIsNull(user.getId(), pageable);

        return wrapChallenges2Container(ret, challenges);
    }

    // TODO: Modify to return last 4 fields
    private Map<String, Object> wrapChallenges2Container(Map<String, Object> ret, Page<Challenge> challenges) {
        List<ChallengeSummary> items = challenges.getContent().stream().map(challenge -> new ChallengeSummary(
            challenge.getId(),
            challenge.getTitle(),
            challenge.getBannerPath(),
            challenge.getDescription(),
            challenge.getLevel().name(),
            challenge.getPoints(),
            challenge.getConfigurations().stream().map(config -> config.getLanguage().getName().name()).collect(Collectors.toList()),
            challenge.getCreatedAt().toString(),
            5L,
            10L,
            "Duy Nguyen",
            List.of("Algorithm", "Complexity"),
            4.2f,
            35
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

    @Override
    public Long updateChallengeMetadata(Long id, ChallengeMetadataRequest request, User user) {
        Challenge challenge = repository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", id));

        if (!challenge.getCreatedBy().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not the owner of this challenge");
        }

        String bannerPath = null;

        try {
            if (request.getBanner() != null && !request.getBanner().isEmpty()) {
                bannerPath = storeFiles(request.getBanner(), user);
            }
        } catch (IOException e) {
            log.error("Error when storing banner image of challenge having id - {}. Message: {}", id, e.getMessage());
        }

        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setLevel(ChallengeLevel.valueOf(request.getLevel().toUpperCase()));
        challenge.setBannerPath(bannerPath != null ? bannerPath : challenge.getBannerPath());

        return repository.save(challenge).getId();
    }

    @Override
    @Transactional
    public Map<String, Object> adjustTestcases(Long challengeId, TestcaseRequest request, User user) {
        Challenge challenge = repository.findByIdAndDeletedAtIsNull(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", challengeId));

        if (!challenge.getCreatedBy().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not the owner of this challenge");
        }

        int numValidTestcases = 0;
        int numInvalidTestcases = 0;
        List<String> invalidTestcases = new ArrayList<>();

        for (Testcase testcase : request.getTestcases()) {
            if (Pattern.matches(challenge.getTestcaseInputFormat(), testcase.getInput()) &&
                Pattern.matches(challenge.getTestcaseOutputFormat(), testcase.getOutput())) {
                challenge.addTestcase(new ChallengeTestcase(testcase.getInput(), testcase.getOutput(), testcase.getPublicTestcase()));
                numValidTestcases++;
            } else {
                numInvalidTestcases++;
                invalidTestcases.add(testcase.toString());
            }
        }

        return Map.of(
            "numValidTestcases", numValidTestcases,
            "numInvalidTestcases", numInvalidTestcases,
            "invalidTestcases", invalidTestcases
        );
    }

    @Override
    @Transactional
    public void updateLanguageConfig(Long challengeId, ChallengeConfigRequest request, User user) {
        Map<String, Object> errors = validate(request);

        if (!errors.isEmpty()) {
            throw new MethodArgumentNotValidException(errors);
        }

        List<String> removedLanguages = request.getRemovedLanguages().stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());

        if (removedLanguages.contains(request.getLanguage().toUpperCase())) {
            throw new BadRequestException("Cannot modify and delete the same config simultaneously");
        }

        Challenge challenge = repository.findByIdAndDeletedAtIsNull(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", challengeId));

        if (!challenge.getCreatedBy().getId().equals(user.getId()) && !user.getAuthorities().contains(UserType.ADMIN)) {
            throw new ForbiddenException("Only admin and challenge's owner can do this task");
        }

        /*
        * For each request, user can only add/update a single challenge config, so if this flag is set to true,
        * the process should be ignored
        * */
        boolean updated = false;

        List<ChallengeConfig> removedConfigs = new ArrayList<>();
        List<ChallengeConfig> addedConfigs = new ArrayList<>();

        for (ChallengeConfig config : challenge.getConfigurations()) {
            String language = config.getLanguage().getName().name();
            if (language.equalsIgnoreCase(request.getLanguage())) { // Config has been already existing
                if (removedLanguages.contains(language)) { // Remove config conditionally
                    removedConfigs.add(config);
                }

                if (!updated) {
                    config.setRunPath(request.getRunPath());
                    config.setCompilePath(request.getCompilePath());
                    config.setImplementedPath(request.getImplementedPath());
                    config.setNonImplementedPath(request.getNonImplementedPath());
                    updated = true;
                }
            } else {
                if (!updated) {
                    addedConfigs.add(new ChallengeConfig(
                        challenge,
                        inMemoryService.challengeLanguages().get(ChallengeLanguage.valueOf(language)),
                        request.getRunPath(),
                        request.getCompilePath(),
                        request.getImplementedPath(),
                        request.getNonImplementedPath(),
                        request.getChallengeDir()
                    ));
                    updated = true;
                }
            }
        }

        /*
        * We do not remove config inside the above loop to avoid {ConcurrentModificationException}
        * */
        challenge.removeConfigs(removedConfigs);
        challenge.addConfigs(addedConfigs);

        repository.save(challenge);
    }

    private Map<String, Object> validate(ChallengeConfigRequest request) {
        if (!StringUtils.hasText(request.getCrudaction())) {
            return Map.of("crudaction", request.getCrudaction());
        }

        Map<String, Object> ret = new HashMap<>();

        if (List.of("remove", "update", "update/remove").contains(request.getCrudaction())) {
            return Map.of("crudaction", Pair.of(request.getCrudaction(), "Should be one of those [\"remove\", \"update\", \"update/remove\"]"));
        }

        switch (request.getCrudaction().toLowerCase()) {
            case "update":
                validateUpdateConfigRequest(request, ret);
                break;
            case "remove":
                validateRemoveConfigRequest(request, ret);
                break;
            case "update/remove":
                validateUpdateConfigRequest(request, ret);
                validateRemoveConfigRequest(request, ret);
            default:
        }

        return ret;
    }

    private void validateUpdateConfigRequest(ChallengeConfigRequest request, Map<String, Object> bindingResult) {
        if (!StringUtils.hasText(request.getLanguage())) {
            bindingResult.put("language", request.getLanguage());
        } else {
            try {
                ChallengeLanguage.valueOf(request.getLanguage().toUpperCase());
            } catch (IllegalArgumentException e) {
                bindingResult.put("language", Pair.of(request.getLanguage(), "Given value does not belong to corresponding enum"));
            }
        }

        if (!StringUtils.hasText(request.getRunPath())) {
            bindingResult.put("runPath", request.getRunPath());
        }

        if (!StringUtils.hasText(request.getCompilePath())) {
            bindingResult.put("compilePath", request.getCompilePath());
        }

        if (!StringUtils.hasText(request.getImplementedPath())) {
            bindingResult.put("implementedPath", request.getImplementedPath());
        }

        if (!StringUtils.hasText(request.getNonImplementedPath())) {
            bindingResult.put("nonImplementedPath", request.getNonImplementedPath());
        }
    }

    private void validateRemoveConfigRequest(ChallengeConfigRequest request, Map<String, Object> bindingResult) {
        if (request.getRemovedLanguages() == null || request.getRemovedLanguages().isEmpty()) {
            bindingResult.put("removedLanguages", request.getRemovedLanguages());
        }
    }

    @Override
    public void deleteChallengeSoftly(Long challengeId, User user) {
        Challenge challenge = repository.findByIdAndDeletedAtIsNull(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", challengeId));

        if (!challenge.getCreatedBy().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not the owner of this challenge");
        }

        challenge.setDeletedAt(LocalDateTime.now());

        repository.save(challenge);
    }
}

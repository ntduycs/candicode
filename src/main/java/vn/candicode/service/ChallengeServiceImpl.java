package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.EntityConstants;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ChallengeConfigurationEntity;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.exception.StorageException;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.request.UpdateChallengeRequest;
import vn.candicode.payload.response.ChallengeDetails;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.sub.Challenge;
import vn.candicode.repository.*;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.ChallengeBeanUtils;
import vn.candicode.util.FileUtils;
import vn.candicode.util.RegexUtils;
import vn.candicode.util.TestcaseBeanUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static vn.candicode.common.FileStorageType.*;

@Service
@Log4j2
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ChallengeCommentRepository challengeCommentRepository;
    private final SubmissionRepository submissionRepository;
    private final SummaryRepository summaryRepository;

    private final StorageService storageService;
    private final CommonService commonService;

    @PersistenceContext
    private EntityManager entityManager;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeConfigurationRepository challengeConfigurationRepository, ChallengeCommentRepository challengeCommentRepository, SubmissionRepository submissionRepository, LanguageRepository languageRepository, SummaryRepository summaryRepository, StorageService storageService, CommonService commonService) {
        this.challengeRepository = challengeRepository;
        this.challengeCommentRepository = challengeCommentRepository;
        this.submissionRepository = submissionRepository;
        this.summaryRepository = summaryRepository;

        this.storageService = storageService;
        this.commonService = commonService;
    }

    /**
     * @param payload challenge payload
     * @param me      challenge owner
     * @return id of new challenge
     * @throws BadRequestException  if you're not the owner of this challenge
     * @throws PersistenceException if the challenge has already existing
     */
    @Override
    @Transactional
    public Map<String, Object> createChallenge(NewChallengeRequest payload, UserPrincipal me) {
        Long myUserId = me.getUserId();

        if (!myUserId.equals(storageService.getDirOwner(payload.getChallengeDir()))) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        Map<String, Object> response = new LinkedHashMap<>();

        String bannerPath = null;
        try {
            if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
                bannerPath = storageService.store(payload.getBanner(), BANNER, myUserId);
            }
        } catch (IOException e) {
            log.error("I/O error. Message - Cannot store file {}", e.getLocalizedMessage());
            response.put("errors", List.of("Failed to store challenge banner"));
        }

        ChallengeEntity challenge = new ChallengeEntity();

        challenge.setTitle(payload.getTitle());
        challenge.setDescription(payload.getDescription());
        challenge.setInputFormat(RegexUtils.genRegex(payload.getTcInputFormat()));
        challenge.setOutputFormat(RegexUtils.genRegex(payload.getTcOutputFormat()));
        challenge.setLevel(payload.getLevel().toLowerCase());
        challenge.setMaxPoint(challenge.getLevel());
        challenge.setAuthor(me.getEntityRef());
        challenge.setAuthorName(me.getFullName());
        if (bannerPath != null) {
            challenge.setBanner(storageService.simplifyPath(bannerPath, BANNER, myUserId));
        }
        challenge.setTags(payload.getTags());
        challenge.setContestChallenge(payload.getContestChallenge());

        if (payload.getCategories() != null) {
            payload.getCategories().stream()
                .filter(c -> commonService.getCategories().containsKey(c))
                .forEach(e -> challenge.addCategory(commonService.getCategories().get(e)));
        }

        try {
            entityManager.persist(challenge);
        } catch (EntityExistsException e) {
            log.error("Challenge has already exist with title = {}", payload.getTitle());
            throw new PersistenceException("Challenge has already exist with title " + payload.getTitle());
        }

        ChallengeConfigurationEntity challengeConfig = new ChallengeConfigurationEntity();

        challengeConfig.setChallenge(challenge);

        String language = payload.getLanguage().toLowerCase();

        if (!commonService.getLanguages().containsKey(language)) {
            log.error("Language {} not found", language);
            throw new PersistenceException("No language with name '" + payload.getLanguage() + "' found or not supported");
        }

        challengeConfig.setLanguage(commonService.getLanguages().get(language));
        challengeConfig.setDirectory(payload.getChallengeDir());
        challengeConfig.setPreImplementedFile(payload.getImplementedPath().substring(1)); // Remove redundant trailing
        challengeConfig.setNonImplementedFile(payload.getNonImplementedPath().substring(1));
        challengeConfig.setRunScript(payload.getRunPath().substring(1));
        challengeConfig.setAuthorId(myUserId);

        /*
         * Root dir is the folder that the run script is placed in
         * */
        String rootDir = Paths.get(payload.getRunPath()).getParent().toString().substring(1);
        challengeConfig.setRoot(rootDir);

        if (payload.getCompilePath() != null) {
            challengeConfig.setCompileScript(payload.getCompilePath().substring(1));
        }

        try {
            entityManager.persist(challengeConfig);
        } catch (EntityExistsException e) {
            log.error("Challenge has already existing with language {}", language);
            throw new PersistenceException("Challenge has already exist with language " + language);
        }

        response.put("challengeId", challenge.getChallengeId());

        return response;
    }

    /**
     * @param file must be a zip file
     * @param me must be not null
     * @return directory tree of submitted source
     * @throws StorageException if has error when parsing
     */
    @Override
    @Transactional
    public DirectoryTree storeChallengeSource(MultipartFile file, UserPrincipal me) {
        try {
            // At first, challenge source will stored at /staging/id folder for waiting to be tested
            String tempChallengeDir = storageService.store(file, STAGING, me.getUserId());
            String challengeDirname = tempChallengeDir.substring(tempChallengeDir.lastIndexOf(File.separator) + 1);

            DirectoryTree tree = new DirectoryTree();
            tree.setChallengeDir(challengeDirname);
            tree.setChildren(storageService.parse(tempChallengeDir, challengeDirname));

            return tree;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        }
    }

    /**
     *
     * @param pageable paginated parameters
     * @return paginated list of challenges
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ChallengeSummary> getChallengeList(Pageable pageable) {
        Page<ChallengeEntity> items = challengeRepository.findAllThatIsNotContestChallenge(pageable);

        List<ChallengeSummary> summaries = items.map(ChallengeBeanUtils::summarize).getContent();

        if (summaries.isEmpty()) {
            //noinspection unchecked
            return (PaginatedResponse<ChallengeSummary>) PaginatedResponse.empty();
        }

        List<Long> challengeIds = summaries.stream().map(ChallengeSummary::getChallengeId).collect(Collectors.toList());

        Map<Long, Long> commentCountMap = summaryRepository.countNumCommentsGroupByChallengeId(challengeIds);
        Map<Long, Long> submissionCountMap = summaryRepository.countNumSubmissionsGroupByChallengeId(challengeIds);
        Map<Long, List<String>> languageNamesMap = summaryRepository.findAllLanguagesByChallengeId(challengeIds);
        Map<Long, List<String>> categoryNamesMap = summaryRepository.findAllCategoriesByChallengeId(challengeIds);

        summaries.forEach(item -> {
            final long challengeId = item.getChallengeId();
            item.setNumComments(commentCountMap.get(challengeId));
            item.setNumAttendees(submissionCountMap.get(challengeId));
            item.setLanguages(languageNamesMap.get(challengeId));
            item.setCategories(categoryNamesMap.get(challengeId));
        });

        return PaginatedResponse.<ChallengeSummary>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    /**
     * @param pageable paginated parameters
     * @param myId must be not null
     * @param wantContestChallenge should load only contest challenge ?
     * @return paginated list of my challenges
     */
    @Transactional(readOnly = true)
    @Override
    public PaginatedResponse<ChallengeSummary> getMyChallengeList(Pageable pageable, Long myId, Boolean wantContestChallenge) {
        Page<ChallengeEntity> items;
        if (wantContestChallenge) {
            items = challengeRepository.findAllContestChallengesByAuthorId(myId, pageable);
        } else {
            items = challengeRepository.findAllByAuthorId(myId, pageable);
        }

        List<ChallengeSummary> summaries = items.map(ChallengeBeanUtils::summarize).getContent();

        if (summaries.isEmpty()) {
            //noinspection unchecked
            return (PaginatedResponse<ChallengeSummary>) PaginatedResponse.empty();
        }

        List<Long> challengeIds = summaries.stream().map(ChallengeSummary::getChallengeId).collect(Collectors.toList());

        Map<Long, Long> commentCountMap = summaryRepository.countNumCommentsGroupByChallengeId(challengeIds);
        Map<Long, Long> submissionCountMap = summaryRepository.countNumSubmissionsGroupByChallengeId(challengeIds);
        Map<Long, List<String>> languageNamesMap = summaryRepository.findAllLanguagesByChallengeId(challengeIds);
        Map<Long, List<String>> categoryNamesMap = summaryRepository.findAllCategoriesByChallengeId(challengeIds);

        summaries.forEach(item -> {
            final long challengeId = item.getChallengeId();
            item.setNumComments(commentCountMap.get(challengeId));
            item.setNumAttendees(submissionCountMap.get(challengeId));
            item.setLanguages(languageNamesMap.get(challengeId));
            item.setCategories(categoryNamesMap.get(challengeId));
        });

        return PaginatedResponse.<ChallengeSummary>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    /**
     * @param challengeId must be not null
     * @return details of challenge with given id
     */
    @Override
    @Transactional(readOnly = true)
    public ChallengeDetails getChallengeDetails(Long challengeId, UserPrincipal me) {
        ChallengeEntity challenge = challengeRepository.findByChallengeId(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        // Only return unavailable challenge for its owner
        if (!challenge.getAvailable() && (me == null || !isMyChallenge(challenge, me))) {
            throw new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId);
        }

        ChallengeDetails details = ChallengeBeanUtils.details(challenge);

        // Only show testcase output if it is public testcase, or owner or vip user is querying
        challenge.getTestcases().stream()
            .map(testcase -> TestcaseBeanUtils.details(testcase, me != null && (isMyChallenge(challenge, me) || isVipUser(me))))
            .forEach(item -> details.getTestcases().add(item));

        Map<String, CompletableFuture<Void>> readChallengeContentProcesses = new HashMap<>();

        List<Challenge> challengeContents = new ArrayList<>();
        for (ChallengeConfigurationEntity configuration : challenge.getConfigurations()) {
            String nonImplementedFile;
            String relativePath = configuration.getDirectory() + File.separator + configuration.getNonImplementedFile();
            if (!configuration.getEnabled()) {
                nonImplementedFile = storageService.resolvePath(relativePath, STAGING, challenge.getAuthor().getUserId());
            } else {
                nonImplementedFile = storageService.resolvePath(relativePath, CHALLENGE, challenge.getAuthor().getUserId());
            }

            CompletableFuture<Void> readProcess = CompletableFuture
                .supplyAsync(() -> FileUtils.readFileToString(new File(nonImplementedFile)))
                .thenAcceptAsync(content -> challengeContents.add(new Challenge(configuration.getLanguage().getName(), content)));

            readChallengeContentProcesses.put(configuration.getLanguage().getName(), readProcess);
        }

        try {
            CompletableFuture.allOf(readChallengeContentProcesses.values().toArray(new CompletableFuture[0])).join();
        } catch (CompletionException e) {
            log.error("Some challenge content files cannot be read. Message - {}", e.getCause().getMessage());
        }

        details.setContents(challengeContents);

        // No need to execute queries if challenge is unavailable
        long numComments = challenge.getAvailable() ? challengeCommentRepository.countByChallenge(challenge) : 0;
        long numAttendees = challenge.getAvailable() ? submissionRepository.countByChallenge(challenge) : 0;

        details.setNumComments(numComments);
        details.setNumAttendees(numAttendees);

        return details;
    }

    private boolean isMyChallenge(ChallengeEntity challenge, UserPrincipal me) {
        return challenge.getAuthor().getUserId().equals(me.getUserId());
    }

    private boolean isVipUser(UserPrincipal me) {
        return me.getAuthorities().contains(new SimpleGrantedAuthority("challenge creator"));
    }

    /**
     * Only author can edit challenge
     *
     * @param challengeId must be not null
     * @param payload     update payload
     * @param me          determine if you're the owner of challenge
     * @throws ResourceNotFoundException if challenge not found
     * @throws BadRequestException       if title has been used by another challenge or if you're not challenge's owner
     */
    @Override
    @Transactional
    public Map<String, Object> updateChallenge(Long challengeId, UpdateChallengeRequest payload, UserPrincipal me) {
        Map<String, Object> response = new HashMap<>();

        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchCategories(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!challenge.getAuthor().getUserId().equals(me.getUserId())) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        if (payload.getTitle() != null && !challenge.getTitle().equals(payload.getTitle())) {
            if (challengeRepository.existsByTitle(payload.getTitle())) {
                throw new BadRequestException("Challenge has been already exist with title " + payload.getTitle());
            }
            challenge.setTitle(payload.getTitle());
        }

        if (payload.getLevel() != null
            && !challenge.getLevel().equals(payload.getLevel())
            && EntityConstants.LEVELS.contains(payload.getLevel())) {
            challenge.setLevel(payload.getLevel());
            challenge.setMaxPoint(payload.getLevel());
        }

        if (payload.getDescription() != null) {
            challenge.setDescription(payload.getDescription());
        }

        if (payload.getTags() != null) {
            challenge.setTags(payload.getTags());
        }

        if (payload.getContestChallenge() != null) {
            challenge.setContestChallenge(payload.getContestChallenge());
        }

        if (payload.getCategories() != null) {
            Set<String> existingCategories = !challenge.getCategories().isEmpty()
                ? challenge.getCategories().stream()
                .map(c -> c.getCategory().getName()).collect(Collectors.toSet())
                : new HashSet<>();

            Set<String> newCategories = payload.getCategories().stream()
                .filter(c -> !existingCategories.contains(c))
                .collect(Collectors.toSet());

            existingCategories.stream()
                .filter(c -> !payload.getCategories().contains(c)) // Filter existing categories that not be included in this update
                .forEach(c -> {
                    if (commonService.getCategories().containsKey(c)) {
                        challenge.removeCategory(commonService.getCategories().get(c));
                    }
                }); // Remove them

            newCategories.forEach(c -> {
                if (commonService.getCategories().containsKey(c)) {
                    challenge.addCategory(commonService.getCategories().get(c));
                }
            });
        }

        if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
            try {
                if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
                    String bannerPath = storageService.store(payload.getBanner(), BANNER, me.getUserId());
                    storageService.delete(challenge.getBanner(), BANNER, me.getUserId());
                    challenge.setBanner(storageService.simplifyPath(bannerPath, BANNER, me.getUserId()));
                }
            } catch (IOException e) {
                log.error("I/O error. Message - Cannot store file {}", e.getLocalizedMessage());
                response.put("errors", List.of("Failed to store challenge banner"));
            }
        }

        challengeRepository.save(challenge);

        response.put("success", true);

        return response;
    }

    /**
     * <ul>
     *     <li>Only author and super admin can delete his challenge</li>
     *     <li>Call this method will only delete the record softly</li>
     * </ul>
     *  @param challengeId must be not null
     *
     * @param me used to check if you're the owner of challenge
     *
     * @throws BadRequestException if you are not the owner of challenge
     */
    @Override
    @Transactional
    public void deleteChallenge(Long challengeId, UserPrincipal me) {
        ChallengeEntity challenge = challengeRepository.findByChallengeIdForDelete(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        if (!isMyChallenge(challenge, me) || !me.getAuthorities().contains(new SimpleGrantedAuthority("super admin"))) {
            throw new BadRequestException("You are not the owner of this challenge");
        }

        challenge.setDeleted(true);
    }
}

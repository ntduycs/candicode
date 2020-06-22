package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.ContestEntity;
import vn.candicode.entity.ContestRoundEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.exception.StorageException;
import vn.candicode.payload.request.NewContestRequest;
import vn.candicode.payload.request.NewContestRoundRequest;
import vn.candicode.payload.request.UpdateContestRequest;
import vn.candicode.payload.response.ContestSummary;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.ContestRepository;
import vn.candicode.repository.ContestRoundRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.DatetimeUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileStorageType.BANNER;

@Service
@Log4j2
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final ContestRoundRepository contestRoundRepository;
    private final ChallengeRepository challengeRepository;

    private final StorageService storageService;

    @PersistenceContext
    private EntityManager entityManager;

    public ContestServiceImpl(ContestRepository contestRepository, ContestRoundRepository contestRoundRepository, ChallengeRepository challengeRepository, StorageService storageService) {
        this.contestRepository = contestRepository;
        this.contestRoundRepository = contestRoundRepository;
        this.challengeRepository = challengeRepository;
        this.storageService = storageService;
    }

    /**
     * @param payload
     * @param author
     * @return id of new contest
     */
    @Override
    @Transactional
    public Long createContest(NewContestRequest payload, UserPrincipal author) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.store(payload.getBanner(), BANNER, author.getUserId());
            }

            ContestEntity contest = new ContestEntity();

            contest.setTitle(payload.getTitle());
            contest.setBanner(bannerPath);
            contest.setAuthor(author.getEntityRef());
            contest.setDescription(payload.getDescription());
            contest.setMaxRegister(payload.getMaxRegister());
            contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.JSON_DATETIME_FORMAT));
            contest.setTags(payload.getTags());
            contest.setContent(payload.getContent());

            entityManager.persist(contest);

            int roundNumber = 1;
            List<ContestRoundEntity> contestRounds = new ArrayList<>();
            for (NewContestRoundRequest roundConfig : payload.getRounds()) {
                ContestRoundEntity round = new ContestRoundEntity();

                if (StringUtils.hasText(roundConfig.getName())) {
                    round.setName(roundConfig.getName());
                } else {
                    round.setName("Round " + roundNumber++);
                }
                round.setContest(contest);

                LocalDateTime startsAt = LocalDateTime.parse(roundConfig.getStartsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
                LocalDateTime endsAt = LocalDateTime.parse(roundConfig.getEndsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
                round.setStartsAt(startsAt);
                round.setDuration(ChronoUnit.MINUTES.between(startsAt, endsAt));

                List<ChallengeEntity> roundChallenges = challengeRepository.findAllByContestChallengeByChallengeIdIn(roundConfig.getChallenges());

                if (roundChallenges.size() != roundConfig.getChallenges().size()) { // Guarantee that all challenges is contest challenge and existing
                    throw new PersistenceException("Contest challenge(s) not found or invalid");
                } else {
                    roundChallenges.forEach(round::addChallenge);
                }

                contestRounds.add(round);
            }

            contestRounds.forEach(contest::addRound);

            entityManager.persist(contest);

            return contest.getContestId();
        } catch (IOException e) {
            log.error("I/O error - cannot store contest banner. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        } catch (EntityExistsException e) {
            log.error("Entity has already existing. Message - {}", e.getLocalizedMessage());
            throw new PersistenceException(e.getLocalizedMessage());
        }
    }

    /**
     * @param contestId
     * @param payload
     * @param author
     */
    @Override
    @Transactional
    public void updateContest(Long contestId, UpdateContestRequest payload, UserPrincipal author) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.store(payload.getBanner(), BANNER, author.getUserId());
            }

            ContestEntity contest = contestRepository.findByContestId(contestId)
                .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

            if (!contest.getTitle().equals(payload.getTitle())) {
                if (contestRepository.existsByTitle(payload.getTitle())) {
                    throw new PersistenceException("Challenge has been already exist with tile" + payload.getTitle());
                } else {
                    contest.setTitle(payload.getTitle());
                }
            }

            if (bannerPath != null) {
                contest.setBanner(bannerPath);
            }

            contest.setAuthor(author.getEntityRef());
            contest.setDescription(payload.getDescription());
            contest.setMaxRegister(payload.getMaxRegister());
            contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.JSON_DATETIME_FORMAT));
            contest.setTags(payload.getTags());
            contest.setContent(payload.getContent());

            contestRepository.save(contest);
        } catch (IOException e) {
            log.error("I/O error - cannot store contest banner. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        }
    }

    /**
     * @param pageable
     * @return paginated list of contests
     */
    @Override
    public PaginatedResponse<ContestSummary> getContestList(Pageable pageable) {
        return null;
    }

    /**
     * @param pageable
     * @param myId
     * @return paginated list of my contests
     */
    @Override
    public PaginatedResponse<ContestSummary> getMyContestList(Pageable pageable, Long myId) {
        return null;
    }
}

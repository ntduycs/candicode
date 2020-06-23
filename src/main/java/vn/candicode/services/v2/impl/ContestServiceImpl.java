package vn.candicode.services.v2.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.exceptions.EntityNotFoundException;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.ContestEntity;
import vn.candicode.models.ContestRoundEntity;
import vn.candicode.payloads.requests.ContestRound;
import vn.candicode.payloads.requests.NewContestRequest;
import vn.candicode.payloads.requests.NewRoundsRequest;
import vn.candicode.payloads.requests.UpdateContestRequest;
import vn.candicode.payloads.responses.*;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.repositories.ContestRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.v1.StorageService;
import vn.candicode.services.v2.ContestService;
import vn.candicode.utils.DatetimeUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final ChallengeRepository challengeRepository;

    private final StorageService storageService;

    public ContestServiceImpl(ContestRepository contestRepository, ChallengeRepository challengeRepository, StorageService storageService) {
        this.contestRepository = contestRepository;
        this.challengeRepository = challengeRepository;
        this.storageService = storageService;
    }

    /**
     * @param payload
     * @param author
     * @return id of new contest
     */
    @Override
    public Long createContest(NewContestRequest payload, UserPrincipal author) {
        if (contestRepository.existsByTitle(payload.getTitle())) {
            throw new PersistenceException("Contest already existing with title " + payload.getTitle());
        }

        String bannerPath;
        try {
            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.storeChallengeBanner(payload.getBanner(), author.getUserId());
            }
        } catch (IOException e) {
            bannerPath = null;
        }

        ContestEntity contest = new ContestEntity();

        contest.setTitle(payload.getTitle());
        contest.setAuthor(author.getEntityRef());
        contest.setBanner(bannerPath);
        contest.setContent(payload.getContent());
        contest.setDescription(payload.getDescription());
        contest.setMaxRegister(payload.getMaxRegister());
        contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.DEFAULT_DATETIME_FORMAT));
        contest.setTags(payload.getTags());

        contestRepository.save(contest);

        return contest.getContestId();
    }

    /**
     * @param contestId
     * @param payload
     * @param currentUser Only contest's owner can perform this operation
     */
    @Override
    public void updateContest(Long contestId, UpdateContestRequest payload, UserPrincipal currentUser) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new EntityNotFoundException("Contest", "id", contestId));

        if (!contest.getTitle().equals(payload.getTitle())) {
            if (contestRepository.existsByTitle(payload.getTitle())) {
                throw new PersistenceException("Contest has been already exist with title" + payload.getTitle());
            }
            contest.setTitle(payload.getTitle());
        }

        String bannerPath = null;
        try {
            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
            } else {
                bannerPath = storageService.storeChallengeBanner(payload.getBanner(), currentUser.getUserId());
            }
        } catch (IOException ignore) {
        }

        if (bannerPath != null) {
            contest.setBanner(bannerPath);
        }

        contest.setContent(payload.getContent());
        contest.setDescription(payload.getDescription());
        contest.setMaxRegister(payload.getMaxRegister());
        contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.DEFAULT_DATETIME_FORMAT));
        contest.setTags(payload.getTags());

        contestRepository.save(contest);
    }

    /**
     * @param pageable
     * @return paginated list of contests
     */
    @Override
    public PaginatedResponse<ContestSummary> getContestList(Pageable pageable) {
        Page<ContestEntity> contests = contestRepository.findAll(pageable);
        return getContestSummaryPaginatedResponse(contests);
    }

    /**
     * @param pageable
     * @param me
     * @return paginated list of my contests
     */
    @Override
    public PaginatedResponse<ContestSummary> getMyContestList(Pageable pageable, UserPrincipal me) {
        Page<ContestEntity> contests = contestRepository.findAllByAuthor(me.getEntityRef(), pageable);

        return getContestSummaryPaginatedResponse(contests);
    }

    private PaginatedResponse<ContestSummary> getContestSummaryPaginatedResponse(Page<ContestEntity> contests) {
        PaginatedResponse<ContestSummary> response = new PaginatedResponse<>();

        response.setPage(contests.getNumber() + 1);
        response.setSize(contests.getSize());
        response.setTotalElements(contests.getTotalElements());
        response.setTotalPages(contests.getTotalPages());
        response.setFirst(contests.isFirst());
        response.setLast(contests.isLast());

        List<ContestSummary> items = new ArrayList<>();

        for (ContestEntity contest : contests) {
            ContestSummary summary = new ContestSummary();
            summary.setContestId(contest.getContestId());
            summary.setTitle(contest.getTitle());
            summary.setAuthor(contest.getAuthor().getFullName());
            summary.setTags(contest.getTags());
            summary.setCreatedAt(contest.getCreatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            summary.setNumComments(0);
            summary.setMaxRegisters(contest.getMaxRegister());
            summary.setCurrRegisters(contest.getRegistrations().size());
            summary.setDescription(contest.getDescription());
            summary.setRegistrationDeadline(contest.getRegistrationDeadline().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            summary.setBanner(contest.getBanner());

            items.add(summary);
        }

        response.setItems(items);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ContestDetails getContestDetails(Long contestId, UserPrincipal me) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new EntityNotFoundException("Contest", "id", contestId));

        ContestDetails contestDetails = new ContestDetails();
        contestDetails.setAuthor(contest.getAuthor().getFullName());
        contestDetails.setBanner(contest.getBanner());
        contestDetails.setContent(contest.getContent());
        contestDetails.setDescription(contest.getDescription());
        contestDetails.setContestId(contestId);
        contestDetails.setTitle(contest.getTitle());
        contestDetails.setCreatedAt(contest.getCreatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
        contestDetails.setNumComments(0);
        contestDetails.setMaxRegisters(contest.getMaxRegister());
        contestDetails.setCurrRegisters(contest.getRegistrations().size());
        contestDetails.setTags(contest.getTags());
        contestDetails.setRegistrationDeadline(contest.getRegistrationDeadline().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));

        List<RoundInfo> rounds = new ArrayList<>();

        for (ContestRoundEntity entity : contest.getRounds()) {
            RoundInfo round = new RoundInfo();
            round.setChallenges(entity.getChallenges().stream().map(c -> c.getChallenge().getChallengeId()).collect(Collectors.toList()));

            round.setStartsAt(entity.getStartsAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            round.setEndsAt(entity.getStartsAt().plus(entity.getDuration(), ChronoUnit.MINUTES).format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            round.setStatus(entity.getStartsAt().isAfter(LocalDateTime.now()));

            Constraint constraints = new Constraint();
            constraints.setAttendeePercent(80);
            constraints.setScorePercent(50);
            round.setConstraints(constraints);

            rounds.add(round);
        }

        return contestDetails;
    }

    @Override
    public LeaderBoard getLeaderBoard(Long contestId) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new EntityNotFoundException("Contest", "id", contestId));

        LeaderBoard leaderBoard = new LeaderBoard();

        return null;
    }

    @Override
    public void createRound(Long contestId, NewRoundsRequest payload, UserPrincipal me) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new EntityNotFoundException("Contest", "id", contestId));

        Set<Long> challengeIds = new HashSet<>();
        payload.getRounds().stream().map(ContestRound::getChallengeIds).forEach(challengeIds::addAll);

        List<ChallengeEntity> challenges = challengeRepository.findAllByChallengeIdIn(challengeIds);

        List<ContestRound> rounds = payload.getRounds();
        for (int i = 0, roundsSize = rounds.size(); i < roundsSize; i++) {
            ContestRound round = rounds.get(i);
            ContestRoundEntity entity = new ContestRoundEntity();
            entity.setName("Round " + i + "-" + contest);
            entity.setStartsAt(LocalDateTime.parse(round.getStartsAt(), DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            entity.setDuration(Duration.between(LocalDateTime.parse(round.getEndsAt(), DatetimeUtils.DEFAULT_DATETIME_FORMAT), entity.getStartsAt()).toMinutes());
            entity.setChallenges(null);

            contest.addRound(entity);
        }

        contestRepository.save(contest);
    }
}

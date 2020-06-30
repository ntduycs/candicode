package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.ContestChallengeEntity;
import vn.candicode.entity.ContestEntity;
import vn.candicode.entity.ContestRoundEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewRoundListRequest;
import vn.candicode.payload.request.RoundRequest;
import vn.candicode.payload.request.UpdateRoundListRequest;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.ContestRepository;
import vn.candicode.repository.ContestRoundRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.DatetimeUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ContestRoundServiceImpl implements ContestRoundService {
    private final ContestRoundRepository contestRoundRepository;
    private final ContestRepository contestRepository;
    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ContestRoundServiceImpl(ContestRoundRepository contestRoundRepository, ContestRepository contestRepository, ChallengeRepository challengeRepository) {
        this.contestRoundRepository = contestRoundRepository;
        this.contestRepository = contestRepository;
        this.challengeRepository = challengeRepository;
    }

    @Override
    @Transactional
    public void createRounds(Long contestId, NewRoundListRequest payload, UserPrincipal me) {
        ContestEntity contest = contestRepository.findByContestIdFetchRounds(contestId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

        Set<Long> challengeIds = payload.getRounds().stream()
            .flatMap(r -> r.getChallenges().stream())
            .collect(Collectors.toSet());

        Map<Long, ChallengeEntity> challengeMap = challengeRepository.findAllByContestChallengeByChallengeIdIn(challengeIds).stream()
            .collect(Collectors.toMap(ChallengeEntity::getChallengeId, challenge -> challenge));

        for (RoundRequest roundRequest : payload.getRounds()) {
            ContestRoundEntity round = new ContestRoundEntity();

            round.setName("Round " + contest.getRounds().size() + 1);

            LocalDateTime startsAt = LocalDateTime.parse(roundRequest.getStartsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
            LocalDateTime endsAt = LocalDateTime.parse(roundRequest.getEndsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
            round.setStartsAt(startsAt);
            round.setDuration(ChronoUnit.MINUTES.between(startsAt, endsAt));

            for (Long challengeId : roundRequest.getChallenges()) {
                if (challengeMap.containsKey(challengeId)) {
                    round.addChallenge(challengeMap.get(challengeId));
                }
            }

            if (round.getChallenges().isEmpty()) {
                throw new PersistenceException("Round has no challenge.");
            } else {
                contest.addRound(round);
            }
        }

        contest.setAvailable(true);
    }

    @Override
    @Transactional
    public void updateRound(Long contestId, UpdateRoundListRequest payload, UserPrincipal me) {
        Map<Long, RoundRequest> roundMap = payload.getRounds().stream().collect(Collectors.toMap(RoundRequest::getRoundId, i -> i));

        List<ContestRoundEntity> rounds = contestRoundRepository.findAllByRoundIdFetchChallenges(contestId, roundMap.keySet());

        for (ContestRoundEntity round : rounds) {
            RoundRequest roundRequest = roundMap.get(round.getContestRoundId());

            if (!round.getName().equals(roundRequest.getName()) && roundRequest.getName() != null) {
                round.setName(roundRequest.getName());
            }

            LocalDateTime startsAt = LocalDateTime.parse(roundRequest.getStartsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
            LocalDateTime endsAt = LocalDateTime.parse(roundRequest.getEndsAt(), DatetimeUtils.JSON_DATETIME_FORMAT);
            round.setStartsAt(startsAt);
            round.setDuration(ChronoUnit.MINUTES.between(startsAt, endsAt));

            List<Long> existingChallengeIds = round.getChallenges().stream()
                .map(item -> item.getChallenge().getChallengeId())
                .collect(Collectors.toList());

            Set<Long> newChallengeIds = roundRequest.getChallenges();
            List<ChallengeEntity> newChallenges = challengeRepository.findAllByContestChallengeByChallengeIdIn(newChallengeIds);

            List<Long> removedChallengeIds = existingChallengeIds.stream()
                .filter(item -> !newChallengeIds.contains(item))
                .collect(Collectors.toList());

            round.getChallenges().forEach(item -> {
                if (removedChallengeIds.contains(item.getChallenge().getChallengeId())) {
                    round.removeChallenge(item.getChallenge());
                }
            });

            List<ChallengeEntity> existingChallenges = round.getChallenges().stream()
                .map(ContestChallengeEntity::getChallenge)
                .collect(Collectors.toList());

            newChallenges.forEach(item -> {
                if (!existingChallenges.contains(item)) {
                    round.addChallenge(item);
                }
            });
        }
    }

    @Override
    @Transactional
    public void removeRound(Long roundId, UserPrincipal me) {
        ContestRoundEntity round = contestRoundRepository.findByRoundIdFetchChallenges(roundId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestRoundEntity.class, "id", roundId));

        round.setDeleted(true);
    }
}

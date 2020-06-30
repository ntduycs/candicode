package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ContestRoundEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ContestRoundRepository extends JpaRepository<ContestRoundEntity, Long> {
    @Query("SELECT r FROM ContestRoundEntity r JOIN FETCH r.challenges rc JOIN FETCH rc.challenge WHERE r.contestRoundId = :id")
    Optional<ContestRoundEntity> findByRoundIdFetchChallenges(@Param("id") Long id);

    @Query("SELECT r FROM ContestRoundEntity r JOIN FETCH r.challenges rc JOIN FETCH rc.challenge WHERE r.contest.contestId = :id")
    List<ContestRoundEntity> findByContestIdFetchChallenges(@Param("id") Long id);

    @Query("SELECT r FROM ContestRoundEntity r JOIN FETCH r.challenges rc JOIN FETCH rc.challenge WHERE r.contest.contestId = :id AND r.contestRoundId IN (:roundIds)")
    List<ContestRoundEntity> findAllByContestIdAndRoundIdsFetchChallenges(@Param("id") Long contestId, @Param("roundIds") Set<Long> roundIds);

    @Query("SELECT r FROM ContestRoundEntity r WHERE r.contest.contestId = :id AND r.contestRoundId IN (:roundIds)")
    List<ContestRoundEntity> findAllByContestIdAndRoundIds(@Param("id") Long contestId, @Param("roundIds") List<Long> roundIds);
}

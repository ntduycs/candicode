package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ContestRoundEntity;

import java.util.List;
import java.util.Optional;

public interface ContestRoundRepository extends JpaRepository<ContestRoundEntity, Long> {
    @Query("SELECT r FROM ContestRoundEntity r JOIN FETCH r.challenges rc JOIN FETCH rc.challenge WHERE r.contestRoundId = :id")
    Optional<ContestRoundEntity> findByRoundIdFetchChallenges(@Param("id") Long id);

    @Query("SELECT r FROM ContestRoundEntity r JOIN FETCH r.challenges rc JOIN FETCH rc.challenge WHERE r.contest.contestId = :id")
    List<ContestRoundEntity> findByContestIdFetchChallenges(@Param("id") Long id);
}

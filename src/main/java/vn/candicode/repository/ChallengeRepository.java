package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeEntity;

import java.util.List;
import java.util.Set;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    @Query("SELECT c FROM ChallengeEntity c WHERE c.contestChallenge = TRUE and c.challengeId IN :ids")
    List<ChallengeEntity> findAllByContestChallengeByChallengeIdIn(@Param("ids") Set<Long> challengeIds);
}

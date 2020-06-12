package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Long> {
    List<TestcaseEntity> findAllByChallenge(ChallengeEntity challenge);

    @Query("select t from TestcaseEntity t where t.challenge.challengeId = :challengeId")
    List<TestcaseEntity> findAllByChallengeId(@Param("challengeId") Long challengeId);
}

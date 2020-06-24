package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.TestcaseEntity;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Long> {
    @Query("SELECT t FROM TestcaseEntity t WHERE t.challenge.challengeId = :id")
    List<TestcaseEntity> findAllByChallengeId(@Param("id") Long challengeId);
}

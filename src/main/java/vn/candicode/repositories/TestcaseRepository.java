package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.TestcaseEntity;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Long> {
    List<TestcaseEntity> findAllByChallenge(ChallengeEntity challenge);
}

package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeTestcase;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<ChallengeTestcase, Long> {
    List<ChallengeTestcase> findAllByChallengeAndDeletedAtIsNull(Challenge challenge);
}

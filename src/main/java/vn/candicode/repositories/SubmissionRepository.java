package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.SubmissionEntity;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    Long countAllByChallenge(ChallengeEntity challenge);
}

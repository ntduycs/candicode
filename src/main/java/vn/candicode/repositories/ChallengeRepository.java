package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ChallengeEntity;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    Optional<ChallengeEntity> findChallengeEntityByChallengeId(Long id);
}

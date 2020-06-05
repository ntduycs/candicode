package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;

import java.util.List;

public interface ChallengeConfigRepository extends JpaRepository<ChallengeConfigEntity, Long> {
    List<ChallengeConfigEntity> findAllByChallenge(ChallengeEntity challenge);
}

package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Challenge;
import vn.candicode.models.ChallengeConfig;
import vn.candicode.models.ChallengeLanguage;

import java.util.Optional;

public interface ChallengeConfigRepository extends JpaRepository<ChallengeConfig, Long> {
    Optional<ChallengeConfig> findByChallengeAndLanguage(Challenge challenge, ChallengeLanguage language);
}

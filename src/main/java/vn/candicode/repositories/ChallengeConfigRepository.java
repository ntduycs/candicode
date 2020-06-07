package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.LanguageEntity;

import java.util.List;
import java.util.Optional;

public interface ChallengeConfigRepository extends JpaRepository<ChallengeConfigEntity, Long> {
    List<ChallengeConfigEntity> findAllByChallenge(ChallengeEntity challenge);

    @Query("select c from ChallengeConfigEntity c where c.challenge.challengeId = ?1 and c.language = ?2")
    Optional<ChallengeConfigEntity> findByChallengeAndLanguage(Long challengeId, LanguageEntity language);
}

package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeConfigurationEntity;

import java.util.List;
import java.util.Optional;

public interface ChallengeConfigurationRepository extends JpaRepository<ChallengeConfigurationEntity, Long> {
    @Query("SELECT c FROM ChallengeConfigurationEntity c JOIN FETCH c.language WHERE c.challenge.challengeId = :id")
    List<ChallengeConfigurationEntity> findAllByChallengeIdFetchLanguage(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeConfigurationEntity c WHERE c.challenge.challengeId = :id")
    List<ChallengeConfigurationEntity> findAllByChallengeId(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeConfigurationEntity c WHERE c.challenge.challengeId = :id AND c.language.name = :name")
    Optional<ChallengeConfigurationEntity> findByChallengeIdAndLanguageName(@Param("id") Long challengeId, @Param("name") String language);
}

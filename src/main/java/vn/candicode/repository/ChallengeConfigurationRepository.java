package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeConfigurationEntity;

import java.util.List;

public interface ChallengeConfigurationRepository extends JpaRepository<ChallengeConfigurationEntity, Long> {
    @Query("SELECT c FROM ChallengeConfigurationEntity c WHERE c.challenge.challengeId = :id")
    List<ChallengeConfigurationEntity> findAllByChallengeId(@Param("id") Long challengeId);
}

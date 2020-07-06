package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.LanguageEntity;

import java.util.List;

public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
    @Query("SELECT l FROM LanguageEntity l, ChallengeConfigurationEntity c " +
        "WHERE l.languageId = c.language.languageId " +
        "AND c.challenge.challengeId = :cid")
    List<LanguageEntity> findAllByChallengeId(@Param("cid") Long challengeId);
}

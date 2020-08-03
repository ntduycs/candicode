package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.CodeExecResultEntity;
import vn.candicode.entity.CodeExecResultId;

import java.util.Optional;

public interface CodeExecResultRepository extends JpaRepository<CodeExecResultEntity, CodeExecResultId> {
    @Query("SELECT cer FROM CodeExecResultEntity cer WHERE cer.submitAt = :submitAt AND cer.challenge.challengeLanguageId = :ccid AND cer.user.userId = :userId")
    Optional<CodeExecResultEntity> findByChallengeConfigIdAndUserIdAndSubmitAt(@Param("ccid") Long challengeConfigId, @Param("userId") Long userId, @Param("submitAt") String submitAt);
}

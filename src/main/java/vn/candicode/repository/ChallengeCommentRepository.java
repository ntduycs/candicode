package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeCommentEntity;

import java.util.Optional;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeCommentEntity, Long> {
    @Query("SELECT c FROM ChallengeCommentEntity c WHERE c.commentId = :cid AND c.challenge.challengeId = :clid")
    Optional<ChallengeCommentEntity> findByCommentIdAndChallengeId(@Param("cid") Long commentId, @Param("clid") Long challengeId);
}

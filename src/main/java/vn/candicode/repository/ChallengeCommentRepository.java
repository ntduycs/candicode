package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeCommentEntity;
import vn.candicode.entity.ChallengeEntity;

import java.util.Optional;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeCommentEntity, Long> {
    @Query("SELECT c FROM ChallengeCommentEntity c WHERE c.commentId = :cid AND c.challenge.challengeId = :clid")
    Optional<ChallengeCommentEntity> findByCommentIdAndChallengeId(@Param("cid") Long commentId, @Param("clid") Long challengeId);

    @Query("SELECT c FROM ChallengeCommentEntity c WHERE c.challenge.challengeId = :id")
    Page<ChallengeCommentEntity> findAllByChallengeId(@Param("id") Long challengeId, Pageable pageable);

    long countByChallenge(ChallengeEntity challenge);
}

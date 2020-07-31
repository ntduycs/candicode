package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.SubmissionEntity;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    @Query("SELECT s FROM SubmissionEntity s WHERE s.author.userId = :id")
    Page<SubmissionEntity> findAllMySubmissions(@Param("id") Long myId, Pageable pageable);

    @Query("SELECT s FROM SubmissionEntity s WHERE s.challenge.challengeId = :id AND s.author.userId = :uid")
    Page<SubmissionEntity> findAllByChallengeIdAndUserId(@Param("id") Long challengeId, @Param("uid") Long userId, Pageable pageable);

    @Query("SELECT s " +
        "FROM SubmissionEntity s, ContestChallengeEntity r " +
        "WHERE s.challenge.contestChallenge = true AND r.challenge.challengeId = s.challenge.challengeId AND s.challenge.challengeId in (r.challenge.challengeId)")
    Page<SubmissionEntity> findAllByContestRoundId(@Param("id") Long roundId, Pageable pageable);

    @Query("SELECT s FROM SubmissionEntity s WHERE s.challenge.challengeId = :cid AND s.author.userId = :uid")
    Optional<SubmissionEntity> findByChallengeIdAndUserId(@Param("cid") Long challengeId, @Param("uid") Long userId);

    long countByChallenge(ChallengeEntity challenge);

    @Query(nativeQuery = true, value = "SELECT DISTINCT ON (author_id, point) s.* FROM submissions s WHERE s.challenge_id = :cid ORDER BY s.point DESC ")
    Page<SubmissionEntity> getHighestScoreSubmissionByChallengeId(@Param("cid") Long challengeId, Pageable pageable);

    Optional<SubmissionEntity> findBySubmissionIdAndAuthorUserId(Long submissionId, Long userId);
}

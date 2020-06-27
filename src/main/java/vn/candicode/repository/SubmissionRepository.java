package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.SubmissionEntity;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    @Query("SELECT s FROM SubmissionEntity s WHERE s.author.userId = :id")
    Page<SubmissionEntity> findAllMySubmissions(@Param("id") Long myId, Pageable pageable);

    @Query("SELECT s FROM SubmissionEntity s WHERE s.challenge.challengeId = :id")
    Page<SubmissionEntity> findAllByChallengeId(@Param("id") Long challengeId, Pageable pageable);

    @Query("SELECT s " +
        "FROM SubmissionEntity s, ContestChallengeEntity r " +
        "WHERE s.challenge.contestChallenge = true AND r.challenge.challengeId = s.challenge.challengeId AND s.challenge.challengeId in (r.challenge.challengeId)")
    Page<SubmissionEntity> findAllByContestRoundId(@Param("id") Long roundId, Pageable pageable);
}

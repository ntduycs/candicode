package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    @Query("SELECT c FROM ChallengeEntity c WHERE c.contestChallenge = TRUE and c.challengeId IN :ids")
    List<ChallengeEntity> findAllByContestChallengeByChallengeIdIn(@Param("ids") Set<Long> challengeIds);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.author.userId = :id")
    Page<ChallengeEntity> findAllByAuthorId(@Param("id") Long userId, Pageable pageable);

    Optional<ChallengeEntity> findByChallengeId(Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.testcases WHERE c.challengeId = :challengeId")
    Optional<ChallengeEntity> findByChallengeIdFetchTestcases(@Param("challengeId") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.categories b LEFT JOIN FETCH b.category WHERE c.challengeId = :challengeId")
    Optional<ChallengeEntity> findByChallengeIdFetchCategories(@Param("challengeId") Long challengeId);

    Boolean existsByTitle(String title);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.contestChallenge = true AND c.challengeId IN (:ids)")
    List<ChallengeEntity> findAllContestChallengeByChallengeIdIn(@Param("ids") Set<Long> challengeIds);
}

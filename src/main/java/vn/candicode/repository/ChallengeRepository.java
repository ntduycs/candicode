package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.dto.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    @Query("SELECT c FROM ChallengeEntity c WHERE c.contestChallenge = TRUE and c.challengeId IN :ids AND c.deleted = false")
    List<ChallengeEntity> findAllByContestChallengeByChallengeIdIn(@Param("ids") Set<Long> challengeIds);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.author.userId = :id and c.deleted = false ")
    Page<ChallengeEntity> findAllByAuthorId(@Param("id") Long userId, Pageable pageable);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.author.userId = :id AND c.contestChallenge = true and c.deleted = false")
    Page<ChallengeEntity> findAllContestChallengesByAuthorId(@Param("id") Long userId, Pageable pageable);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.challengeId = :id AND c.deleted = false ")
    Optional<ChallengeEntity> findByChallengeId(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.testcases WHERE c.challengeId = :challengeId and c.deleted = false")
    Optional<ChallengeEntity> findByChallengeIdFetchTestcases(@Param("challengeId") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.categories b LEFT JOIN FETCH b.category WHERE c.challengeId = :challengeId and c.deleted = false")
    Optional<ChallengeEntity> findByChallengeIdFetchCategories(@Param("challengeId") Long challengeId);

    Boolean existsByTitle(String title);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.contestChallenge = true AND c.challengeId IN (:ids) and c.deleted = false")
    List<ChallengeEntity> findAllContestChallengeByChallengeIdIn(@Param("ids") Set<Long> challengeIds);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.comments WHERE c.challengeId = :id AND c.deleted = false ")
    Optional<ChallengeEntity> findByChallengeIdFetchComments(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c JOIN c.configurations d JOIN d.language WHERE c.deleted = false and c.contestChallenge = false")
    Page<ChallengeEntity> findAllFetchLanguages(Pageable pageable);

    @Query("SELECT new vn.candicode.entity.dto.Tag(c.challengeId, c.tags) FROM ChallengeEntity c")
    List<Tag> findAllChallengeTags();
}

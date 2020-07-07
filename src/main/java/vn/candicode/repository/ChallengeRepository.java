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

    @Query("SELECT c FROM ChallengeEntity c " +
        "LEFT JOIN FETCH c.categories ct JOIN FETCH ct.category " +
        "WHERE c.challengeId = :id AND c.deleted = false")
    Optional<ChallengeEntity> findByChallengeId(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c JOIN FETCH c.author WHERE c.challengeId = :id")
    Optional<ChallengeEntity> findByChallengeIdFetchAuthor(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c JOIN FETCH c.testcases WHERE c.challengeId = :id")
    Optional<ChallengeEntity> findByChallengeIdFetchTestcases(@Param("id") Long challengeId);

    /**
     * This only is used for update purpose
     */
    @Query("SELECT c FROM ChallengeEntity c JOIN FETCH c.author JOIN FETCH c.testcases WHERE c.challengeId = :id")
    Optional<ChallengeEntity> findByChallengeIdFetchAuthorAndTestcases(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.categories b LEFT JOIN FETCH b.category WHERE c.challengeId = :challengeId and c.deleted = false")
    Optional<ChallengeEntity> findByChallengeIdFetchCategories(@Param("challengeId") Long challengeId);

    Boolean existsByTitle(String title);

    @Query("SELECT c FROM ChallengeEntity c LEFT JOIN FETCH c.comments WHERE c.challengeId = :id AND c.deleted = false ")
    Optional<ChallengeEntity> findByChallengeIdFetchComments(@Param("id") Long challengeId);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.deleted = false AND c.contestChallenge = false AND c.available = true")
    Page<ChallengeEntity> findAllThatIsNotContestChallenge(Pageable pageable);

    @Query("SELECT new vn.candicode.entity.dto.Tag(c.challengeId, c.tags) FROM ChallengeEntity c")
    List<Tag> findAllChallengeTags();

    @Query("SELECT c FROM ChallengeEntity c " +
        "LEFT JOIN FETCH c.configurations cf JOIN FETCH cf.language " +
        "JOIN FETCH c.author " +
        "WHERE c.challengeId = :id AND c.deleted = false")
    Optional<ChallengeEntity> findByChallengeFetchConfigurationsAndAuthor(@Param("id") Long challengeId);
}

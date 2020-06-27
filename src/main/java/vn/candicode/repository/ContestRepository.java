package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ContestEntity;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestEntity, Long> {
    Optional<ContestEntity> findByContestId(Long contestId);

    boolean existsByTitle(String title);

    @Query("SELECT c FROM ContestEntity c LEFT JOIN FETCH c.rounds WHERE c.contestId = :id")
    Optional<ContestEntity> findByContestIdFetchRounds(@Param("id") Long contestId);

    @Query("SELECT c FROM ContestEntity c WHERE c.author.userId = :id")
    Page<ContestEntity> findAllByAuthorId(@Param("id") Long authorId, Pageable pageable);
}

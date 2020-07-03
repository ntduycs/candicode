package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.TutorialEntity;

import java.util.Optional;

public interface TutorialRepository extends JpaRepository<TutorialEntity, Long> {
    @Query("SELECT t FROM TutorialEntity t LEFT JOIN FETCH t.categories b LEFT JOIN FETCH b.category WHERE t.tutorialId = :id")
    Optional<TutorialEntity> findByTutorialIdFetchCategories(@Param("id") Long id);

    boolean existsByTitle(String title);

    @Query("SELECT t FROM TutorialEntity t WHERE t.author.userId = :id")
    Page<TutorialEntity> findAllByAuthorId(@Param("id") Long authorId, Pageable pageable);

    Optional<TutorialEntity> findByTutorialId(Long id);

    @Query("SELECT t FROM TutorialEntity t LEFT JOIN FETCH t.comments WHERE t.tutorialId = :id")
    Optional<TutorialEntity> findByTutorialIdFetchComments(@Param("id") Long id);
}

package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.TutorialEntity;

import java.util.Optional;

public interface TutorialRepository extends JpaRepository<TutorialEntity, Long> {
    @Query("SELECT t FROM TutorialEntity t LEFT JOIN FETCH t.categories WHERE t.tutorialId = :id")
    Optional<TutorialEntity> findByTutorialIdFetchCategories(@Param("id") Long id);

    boolean existsByTitle(String title);
}

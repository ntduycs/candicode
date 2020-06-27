package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.TutorialCommentEntity;

import java.util.Optional;

public interface TutorialCommentRepository extends JpaRepository<TutorialCommentEntity, Long> {
    @Query("SELECT c FROM TutorialCommentEntity c WHERE c.commentId = :cid AND c.tutorial.tutorialId = :tid")
    Optional<TutorialCommentEntity> findByCommentIdAndTutorialId(@Param("cid") Long commentId, @Param("tid") Long tutorialId);

    @Query("SELECT c FROM TutorialCommentEntity c WHERE c.tutorial.tutorialId = :id")
    Page<TutorialCommentEntity> findAllByTutorialId(@Param("id") Long tutorialId, Pageable pageable);
}

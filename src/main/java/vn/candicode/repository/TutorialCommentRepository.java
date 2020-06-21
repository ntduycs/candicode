package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.TutorialCommentEntity;

public interface TutorialCommentRepository extends JpaRepository<TutorialCommentEntity, Long> {
}

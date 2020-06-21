package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.ChallengeCommentEntity;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeCommentEntity, Long> {
}

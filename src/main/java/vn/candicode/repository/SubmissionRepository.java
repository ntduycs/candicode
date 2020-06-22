package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.SubmissionEntity;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
}

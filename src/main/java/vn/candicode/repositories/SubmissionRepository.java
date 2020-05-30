package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}

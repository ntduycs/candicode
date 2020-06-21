package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.TestcaseEntity;

public interface TestcaseRepository extends JpaRepository<TestcaseEntity, Long> {
}

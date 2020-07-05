package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.StudentPlanEntity;

public interface StudentPlanRepository extends JpaRepository<StudentPlanEntity, Long> {
}

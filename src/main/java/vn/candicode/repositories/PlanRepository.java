package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.PlanEntity;

public interface PlanRepository extends JpaRepository<PlanEntity, Long> {
}

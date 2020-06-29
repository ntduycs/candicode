package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.PasswordUpdateEntity;

public interface PasswordUpdateRepository extends JpaRepository<PasswordUpdateEntity, Long> {
}

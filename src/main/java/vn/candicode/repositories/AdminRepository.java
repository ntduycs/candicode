package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.AdminEntity;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    Optional<AdminEntity> findAdminEntityByUserId(Long id);
}

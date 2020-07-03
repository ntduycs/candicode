package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.AdminEntity;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    @Query("SELECT a FROM AdminEntity a LEFT JOIN FETCH a.roles b JOIN FETCH b.role WHERE a.userId = :id")
    Optional<AdminEntity> findByUserIdFetchRoles(@Param("id") Long id);
}

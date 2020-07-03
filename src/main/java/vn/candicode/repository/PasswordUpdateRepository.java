package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.PasswordUpdateEntity;

import java.util.Optional;

public interface PasswordUpdateRepository extends JpaRepository<PasswordUpdateEntity, Long> {

    @Query("SELECT r " +
        "FROM PasswordUpdateEntity r " +
        "WHERE r.user.userId = :id AND " +
        "      r.createdAt = (SELECT max(a.createdAt) " +
        "                     FROM PasswordUpdateEntity a " +
        "                     WHERE a.user.userId = r.user.userId)")
    Optional<PasswordUpdateEntity> findMostRecentPasswordUpdateRequestByUserId(@Param("id") Long userId);
}

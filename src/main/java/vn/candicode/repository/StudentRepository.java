package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.StudentEntity;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("SELECT a FROM StudentEntity a LEFT JOIN FETCH a.roles b JOIN FETCH b.role WHERE a.userId = :id")
    Optional<StudentEntity> findByUserIdFetchRoles(@Param("id") Long id);
}

package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.StudentEntity;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByUserId(Long id);
}

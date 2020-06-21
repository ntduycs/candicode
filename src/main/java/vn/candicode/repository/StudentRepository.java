package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.StudentEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}

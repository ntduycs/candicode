package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.RoleEntity;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    List<RoleEntity> findAllByNameIn(List<String> names);
}

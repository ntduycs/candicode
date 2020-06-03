package vn.candicode.repositories;

import org.springframework.data.repository.CrudRepository;
import vn.candicode.models.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}

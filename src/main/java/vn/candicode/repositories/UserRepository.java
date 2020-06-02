package vn.candicode.repositories;

import org.springframework.data.repository.CrudRepository;
import vn.candicode.models.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findUserEntitiesByEmail(String email);
}

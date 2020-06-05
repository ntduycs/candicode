package vn.candicode.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.candicode.models.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("select user from UserEntity user inner join fetch user.roles where user.email = :email")
    Optional<UserEntity> findByEmailFetchedRoles(String email);

    boolean existsByEmail(String email);
}

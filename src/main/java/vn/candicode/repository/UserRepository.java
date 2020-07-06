package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles r JOIN FETCH r.role WHERE u.email = :email")
    Optional<UserEntity> findByEmailFetchRoles(@Param("email") String email);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUserId(Long userId);
}

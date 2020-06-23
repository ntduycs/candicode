package vn.candicode.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ContestEntity;
import vn.candicode.models.UserEntity;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestEntity, Long> {
    Optional<ContestEntity> findByContestId(Long contestId);

    boolean existsByTitle(String title);

    Page<ContestEntity> findAllByAuthor(UserEntity author, Pageable pageable);
}

package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.ContestEntity;

import java.util.Optional;

public interface ContestRepository extends JpaRepository<ContestEntity, Long> {
    Optional<ContestEntity> findByContestId(Long contestId);

    boolean existsByTitle(String title);
}

package vn.candicode.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Challenge;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByIdAndDeletedAtIsNull(Long id);

    Page<Challenge> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Challenge> findAllByCreatedByAndDeletedAtIsNull(Long userId, Pageable pageable);
}

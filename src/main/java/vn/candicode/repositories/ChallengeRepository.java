package vn.candicode.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.ChallengeEntity;
import vn.candicode.models.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
    Optional<ChallengeEntity> findByChallengeId(Long id);

    Page<ChallengeEntity> findAllByAuthor(UserEntity id, Pageable pageable);

    boolean existsByTitle(String title);

    int countAllByTitle(String title);

    List<ChallengeEntity> findAllByChallengeIdIn(Set<Long> ids);
}

package vn.candicode.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.TutorialEntity;
import vn.candicode.models.UserEntity;

import java.util.Optional;

public interface TutorialRepository extends JpaRepository<TutorialEntity, Long> {
    Optional<TutorialEntity> findByTutorialId(Long id);

    Page<TutorialEntity> findAllByAuthor(UserEntity author, Pageable pageable);
}

package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.LanguageEntity;

public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
}
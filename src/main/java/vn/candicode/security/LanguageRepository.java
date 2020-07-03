package vn.candicode.security;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.LanguageEntity;

public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
}

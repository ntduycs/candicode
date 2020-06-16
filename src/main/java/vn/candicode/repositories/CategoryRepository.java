package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}

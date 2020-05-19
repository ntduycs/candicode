package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Coder;

public interface CoderRepository extends JpaRepository<Coder, Long> {
}

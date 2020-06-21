package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.entity.ChallengeEntity;

public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long> {
}

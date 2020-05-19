package vn.candicode.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.candicode.models.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}

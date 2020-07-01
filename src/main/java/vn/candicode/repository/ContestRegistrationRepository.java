package vn.candicode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.candicode.entity.ContestRegistrationEntity;

public interface ContestRegistrationRepository extends JpaRepository<ContestRegistrationEntity, Long> {
    @Query("SELECT r FROM  ContestRegistrationEntity r WHERE r.contest.contestId = :cid AND r.student.userId = :sid")
    Object findByContestIdAndStudentId(@Param("cid") Long contestId, @Param("sid") Long studentId);
}

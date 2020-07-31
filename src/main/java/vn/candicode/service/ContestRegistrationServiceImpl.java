package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.entity.ContestEntity;
import vn.candicode.entity.StudentEntity;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.repository.ContestRepository;
import vn.candicode.security.UserPrincipal;

@Service
@Log4j2
public class ContestRegistrationServiceImpl implements ContestRegistrationService {
    private final ContestRepository contestRepository;

    public ContestRegistrationServiceImpl(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Override
    @Transactional
    public void enrollContest(Long contestId, UserPrincipal me) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

        contest.addRegistration((StudentEntity) me.getEntityRef());

        log.info("New student has registered the contest with id - {}", contestId);
    }
}

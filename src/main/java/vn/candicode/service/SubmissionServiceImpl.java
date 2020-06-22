package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.SubmissionRepository;
import vn.candicode.security.UserPrincipal;

@Service
@Log4j2
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    /**
     * @param challengeId
     * @param payload
     * @param author      Only student can do this operation
     * @return
     */
    @Override
    public SubmissionSummary doScoreSubmission(Long challengeId, NewSubmissionRequest payload, UserPrincipal author) {
        return null;
    }

    /**
     * @param pageable
     * @param me
     * @return
     */
    @Override
    public PaginatedResponse<SubmissionSummary> getMySubmissionHistory(Pageable pageable, UserPrincipal me) {
        return null;
    }
}

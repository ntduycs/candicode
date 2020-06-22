package vn.candicode.service;

import org.springframework.data.domain.Pageable;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.UserPrincipal;

public interface SubmissionService {
    /**
     * @param challengeId
     * @param payload
     * @param author      Only student can do this operation
     * @return
     */
    SubmissionSummary doScoreSubmission(Long challengeId, NewSubmissionRequest payload, UserPrincipal author);

    /**
     * @param pageable
     * @param me
     * @return
     */
    PaginatedResponse<SubmissionSummary> getMySubmissionHistory(Pageable pageable, UserPrincipal me);
}

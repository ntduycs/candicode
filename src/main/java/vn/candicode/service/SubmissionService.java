package vn.candicode.service;

import org.springframework.data.domain.Pageable;
import vn.candicode.payload.request.NewCodeRunRequest;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionHistory;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.payload.response.SubmittedCode;
import vn.candicode.security.UserPrincipal;

import java.util.List;

public interface SubmissionService {
    /**
     * @param challengeId
     * @param payload
     * @param author      Only student can do this operation
     * @return
     */
    SubmissionSummary doScoreSubmission(Long challengeId, NewCodeRunRequest payload, UserPrincipal author);

    void saveSubmission(Long challengeId, NewSubmissionRequest payload, UserPrincipal me);

    /**
     * @param pageable
     * @param me
     * @return
     */
    PaginatedResponse<SubmissionHistory> getMySubmissionHistory(Pageable pageable, UserPrincipal me);

    PaginatedResponse<SubmissionHistory> getSubmissionsByChallengeAndUser(Pageable pageable, Long challengeId, UserPrincipal user);

    PaginatedResponse<SubmissionHistory> getSubmissionsByContestRound(Pageable pageable, Long roundId);

    List<SubmissionHistory> getRecentSubmissionByUserId(Long userId);

    SubmittedCode getSubmittedCode(Long submissionId, UserPrincipal me);
}

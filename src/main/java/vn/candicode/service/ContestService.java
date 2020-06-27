package vn.candicode.service;

import org.springframework.data.domain.Pageable;
import vn.candicode.payload.request.NewContestRequest;
import vn.candicode.payload.request.UpdateContestRequest;
import vn.candicode.payload.response.ContestSummary;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.security.UserPrincipal;

public interface ContestService {
    /**
     * @param payload
     * @param author
     * @return id of new contest
     */
    Long createContest(NewContestRequest payload, UserPrincipal author);

    /**
     * @param contestId
     * @param payload
     * @param currentUser Only contest's owner can perform this operation
     */
    void updateContest(Long contestId, UpdateContestRequest payload, UserPrincipal currentUser);

    /**
     * Softly delete
     *
     * @param contestId
     * @param me
     */
    void removeContest(Long contestId, UserPrincipal me);

    /**
     * @param pageable
     * @return paginated list of contests
     */
    PaginatedResponse<ContestSummary> getContestList(Pageable pageable);

    /**
     * @param pageable
     * @param myId
     * @return paginated list of my contests
     */
    PaginatedResponse<ContestSummary> getMyContestList(Pageable pageable, Long myId);
}

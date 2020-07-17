package vn.candicode.service;

import vn.candicode.payload.request.ContestPaginatedRequest;
import vn.candicode.payload.request.NewContestRequest;
import vn.candicode.payload.request.UpdateContestRequest;
import vn.candicode.payload.response.ContestDetails;
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
     * @param payload
     * @return paginated list of contests
     */
    PaginatedResponse<ContestSummary> getContestList(ContestPaginatedRequest payload, boolean isAdmin);

    /**
     * @param payload
     * @param myId
     * @return paginated list of my contests
     */
    PaginatedResponse<ContestSummary> getMyContestList(ContestPaginatedRequest payload, Long myId);

    ContestDetails getContestDetails(Long contestId);
}

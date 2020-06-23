package vn.candicode.services.v2;

import org.springframework.data.domain.Pageable;
import vn.candicode.payloads.requests.NewContestRequest;
import vn.candicode.payloads.requests.UpdateContestRequest;
import vn.candicode.payloads.responses.ContestDetails;
import vn.candicode.payloads.responses.ContestSummary;
import vn.candicode.payloads.responses.LeaderBoard;
import vn.candicode.payloads.responses.PaginatedResponse;
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
     * @param pageable
     * @return paginated list of contests
     */
    PaginatedResponse<ContestSummary> getContestList(Pageable pageable);

    /**
     * @param pageable
     * @param me
     * @return paginated list of my contests
     */
    PaginatedResponse<ContestSummary> getMyContestList(Pageable pageable, UserPrincipal me);

    ContestDetails getContestDetails(Long contestId, UserPrincipal me);

    LeaderBoard getLeaderBoard(Long contestId);
}

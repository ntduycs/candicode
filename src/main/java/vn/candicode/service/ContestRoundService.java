package vn.candicode.service;

import vn.candicode.payload.request.NewRoundListRequest;
import vn.candicode.payload.request.UpdateRoundRequest;
import vn.candicode.security.UserPrincipal;

public interface ContestRoundService {
    /**
     * @param contestId
     * @param payload
     * @param me
     */
    void createRounds(Long contestId, NewRoundListRequest payload, UserPrincipal me);

    void updateRound(Long roundId, UpdateRoundRequest payload, UserPrincipal me);

    void removeRound(Long roundId, UserPrincipal me);
}

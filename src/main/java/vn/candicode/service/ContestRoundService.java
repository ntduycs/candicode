package vn.candicode.service;

import vn.candicode.payload.request.NewRoundListRequest;
import vn.candicode.payload.request.UpdateRoundListRequest;
import vn.candicode.security.UserPrincipal;

import java.util.List;

public interface ContestRoundService {
    /**
     * @param contestId
     * @param payload
     * @param me
     */
    void createRounds(Long contestId, NewRoundListRequest payload, UserPrincipal me);

    void updateRound(Long contestId, UpdateRoundListRequest payload, UserPrincipal me);

    void removeRound(Long contestId, List<Long> roundIds, UserPrincipal me);
}

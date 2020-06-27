package vn.candicode.service;

import vn.candicode.payload.request.NewRoundRequest;
import vn.candicode.payload.request.UpdateRoundRequest;
import vn.candicode.security.UserPrincipal;

public interface ContestRoundService {
    void createRound(Long contestId, NewRoundRequest payload, UserPrincipal me);

    void updateRound(Long roundId, UpdateRoundRequest payload, UserPrincipal me);

    void removeRound(Long roundId, UserPrincipal me);
}

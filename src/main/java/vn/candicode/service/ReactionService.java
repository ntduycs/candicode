package vn.candicode.service;

import vn.candicode.payload.request.ReactionRequest;
import vn.candicode.security.UserPrincipal;

public interface ReactionService {
    void storeReaction(ReactionRequest payload, UserPrincipal me);
}

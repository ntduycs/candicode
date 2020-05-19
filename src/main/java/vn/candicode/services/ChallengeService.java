package vn.candicode.services;

import vn.candicode.models.Challenge;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeSummary;

public interface ChallengeService {
    ChallengeSummary createChallenge(ChallengeRequest request);
}

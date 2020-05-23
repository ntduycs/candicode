package vn.candicode.services;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.models.Challenge;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeSummary;

public interface ChallengeService {
    Long createChallenge(ChallengeRequest request, User user);

    Component parseDirTree(MultipartFile sourceCode, User user);
}

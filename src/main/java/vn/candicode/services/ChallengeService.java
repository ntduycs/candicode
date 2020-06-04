package vn.candicode.services;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.responses.SourceCodeStructure;
import vn.candicode.security.UserPrincipal;

public interface ChallengeService {

    /**
     * @param payload
     * @return id of the newly created challenge
     */
    Long createChallenge(NewChallengeRequest payload, UserPrincipal currentUser);

    SourceCodeStructure storeChallengeSourceCode(MultipartFile sourceZipFile, UserPrincipal currentUser);
}

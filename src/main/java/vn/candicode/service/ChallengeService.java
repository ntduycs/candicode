package vn.candicode.service;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.security.UserPrincipal;

public interface ChallengeService {
    /**
     * @param payload
     * @param author
     * @return id of new challenge
     */
    Long createChallenge(NewChallengeRequest payload, UserPrincipal author);

    /**
     * @param file must be a zip file
     * @param author
     * @return
     */
    DirectoryTree storeChallengeSource(MultipartFile file, UserPrincipal author);
}

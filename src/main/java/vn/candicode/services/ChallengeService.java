package vn.candicode.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.models.Challenge;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeDetail;
import vn.candicode.payloads.responses.ChallengeSummary;

import java.util.List;
import java.util.Map;

public interface ChallengeService {
    Long createChallenge(ChallengeRequest request, User user);

    Component parseDirTree(MultipartFile sourceCode, User user);

    ChallengeDetail getChallengeById(Long id);

    Map<String, Object> getChallenges(Pageable pageable);
}

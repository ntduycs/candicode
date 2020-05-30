package vn.candicode.services;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.ChallengeConfigRequest;
import vn.candicode.payloads.requests.ChallengeMetadataRequest;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.requests.TestcaseRequest;
import vn.candicode.payloads.responses.ChallengeDetail;

import java.util.Map;

public interface ChallengeService {
    Long createChallenge(ChallengeRequest request, User user);

    Map<String, Object> parseDirTree(MultipartFile sourceCode, User user);

    ChallengeDetail getChallengeById(Long id);

    Map<String, Object> getChallenges(Pageable pageable);

    Map<String, Object> getMyChallenges(Pageable pageable, User user);

    Long updateChallengeMetadata(Long id, ChallengeMetadataRequest request, User user);

    Map<String, Object> adjustTestcases(Long challengeId, TestcaseRequest request, User user);

    void updateLanguageConfig(Long challengeId, ChallengeConfigRequest request, User user);

    void deleteChallengeSoftly(Long challengeId, User user);
}

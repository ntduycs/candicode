package vn.candicode.services;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.requests.TestcaseVerificationRequest;
import vn.candicode.payloads.requests.TestcasesRequest;
import vn.candicode.payloads.responses.*;
import vn.candicode.security.UserPrincipal;

public interface ChallengeService {

    /**
     * @param payload
     * @return id of the newly created challenge
     */
    Long createChallenge(NewChallengeRequest payload, UserPrincipal currentUser);

    SourceCodeStructure storeChallengeSourceCode(MultipartFile sourceZipFile, UserPrincipal currentUser);

    Integer createTestcases(Long challengeId, TestcasesRequest payload, UserPrincipal currentUser);

    TestcaseVerificationResult verifyTestcase(Long challengeId, TestcaseVerificationRequest payload);

    ChallengeDetails getChallengeDetails(Long challengeId);

    PaginatedResponse<ChallengeSummary> getChallengeList(Pageable pageable);

    SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal currentUser);
}

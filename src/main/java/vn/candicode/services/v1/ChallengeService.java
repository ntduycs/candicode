package vn.candicode.services.v1;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.requests.*;
import vn.candicode.payloads.responses.*;
import vn.candicode.security.UserPrincipal;

import java.util.List;

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

    PaginatedResponse<ChallengeSummary> getMyChallengeList(Pageable pageable, UserPrincipal currentUser);

    SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal currentUser);

    void editChallenge(Long challengeId, EditChallengeRequest payload);

    void deleteChallenge(Long challengeId);

    RemoveTestcasesResult removeTestcases(Long challengeId, List<Long> testcaseIds);

    int updateTestcases(Long challengeId, UpdateTestcasesRequest payload);

    boolean removeLanguage(Long challengeId, String language);

    SubmissionResult addLanguage(Long challengeId, NewLanguageRequest payload, UserPrincipal currentUser);
}

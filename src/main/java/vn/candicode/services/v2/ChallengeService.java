package vn.candicode.services.v2;

import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.requests.TestcaseVerificationRequest;
import vn.candicode.payloads.responses.SubmissionResult;
import vn.candicode.payloads.responses.TestcaseVerificationResult;
import vn.candicode.security.UserPrincipal;

public interface ChallengeService {
    SubmissionResult evaluateSubmission(Long challengeId, SubmissionRequest payload, UserPrincipal user);

    TestcaseVerificationResult verifyTestcase(Long challengeId, TestcaseVerificationRequest payload, UserPrincipal user);
}

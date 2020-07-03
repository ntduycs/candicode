package vn.candicode.service;

import vn.candicode.payload.request.NewTestcaseListRequest;
import vn.candicode.payload.request.UpdateTestcaseListRequest;
import vn.candicode.payload.request.VerificationRequest;
import vn.candicode.payload.response.VerificationSummary;
import vn.candicode.security.UserPrincipal;

import java.util.List;

public interface TestcaseService {
    /**
     * Only challenge's owner can add testcase(s)
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return number of successfully added testcases
     */
    Integer createTestcases(Long challengeId, NewTestcaseListRequest payload, UserPrincipal currentUser);

    /**
     * Only challenge's owner can call to verify testcase on his challenge
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return verification result on multiple supported languages
     */
    VerificationSummary verifyTestcase(Long challengeId, VerificationRequest payload, UserPrincipal currentUser);

    /**
     * Only challenge's owner can update testcases of his challenge
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return number of successfully updated testcases
     */
    Integer updateTestcases(Long challengeId, UpdateTestcaseListRequest payload, UserPrincipal currentUser);

    /**
     * Only challenge's owner can delete his challenge's testcases
     *
     * @param challengeId
     * @param testcaseIds
     * @param currentUser
     * @return an array that contains:
     * <ul>
     *     <li>Number of deleted testcases</li>
     *     <li>Number of remaining testcases</li>
     * </ul>
     */
    Integer[] deleteTestcases(Long challengeId, List<Long> testcaseIds, UserPrincipal currentUser);
}

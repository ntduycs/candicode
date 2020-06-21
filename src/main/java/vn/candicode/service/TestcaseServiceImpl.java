package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.payload.request.NewTestcaseListRequest;
import vn.candicode.payload.request.UpdateTestcaseListRequest;
import vn.candicode.payload.request.VerificationRequest;
import vn.candicode.payload.response.VerificationSummary;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.TestcaseRepository;
import vn.candicode.security.UserPrincipal;

import java.util.List;

@Service
@Log4j2
public class TestcaseServiceImpl implements TestcaseService {
    private final TestcaseRepository testcaseRepository;
    private final ChallengeRepository challengeRepository;

    public TestcaseServiceImpl(TestcaseRepository testcaseRepository, ChallengeRepository challengeRepository) {
        this.testcaseRepository = testcaseRepository;
        this.challengeRepository = challengeRepository;
    }

    /**
     * Only challenge's owner can add testcase(s)
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return number of successfully added testcases
     */
    @Override
    public Integer createTestcases(Long challengeId, NewTestcaseListRequest payload, UserPrincipal currentUser) {
        return null;
    }

    /**
     * Only challenge's owner can call to verify testcase on his challenge
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return verification result on multiple supported languages
     */
    @Override
    public VerificationSummary verifyTestcase(Long challengeId, VerificationRequest payload, UserPrincipal currentUser) {
        return null;
    }

    /**
     * Only challenge's owner can update testcases of his challenge
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return number of successfully updated testcases
     */
    @Override
    public Integer updateTestcases(Long challengeId, UpdateTestcaseListRequest payload, UserPrincipal currentUser) {
        return null;
    }

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
    @Override
    public Integer[] deleteTestcases(Long challengeId, List<Long> testcaseIds, UserPrincipal currentUser) {
        return new Integer[0];
    }
}

package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewTestcaseListRequest;
import vn.candicode.payload.request.VerificationRequest;
import vn.candicode.payload.response.VerificationSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.TestcaseService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class TestcaseController extends Controller {
    private final TestcaseService testcaseService;

    public TestcaseController(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @Override
    protected String getPath() {
        return "testcases";
    }

    /**
     * Verify testcase
     *
     * @param challengeId
     * @param payload
     * @param me
     * @return
     */
    @PostMapping(path = "challenges/{id}/testcases/verification")
    public ResponseEntity<?> verifyTestcase(@PathVariable("id") Long challengeId, @RequestBody @Valid VerificationRequest payload, @CurrentUser UserPrincipal me) {
        VerificationSummary result = testcaseService.verifyTestcase(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(result));
    }

    /**
     * Create multiple testcases
     *
     * @param challengeId
     * @param payload
     * @param me
     * @return
     */
    @PostMapping(path = "challenges/{id}/testcases")
    public ResponseEntity<?> createTestcases(@PathVariable("id") Long challengeId, @RequestBody @Valid NewTestcaseListRequest payload, @CurrentUser UserPrincipal me) {
        Integer numAddedTestcases = testcaseService.createTestcases(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Created " + numAddedTestcases + " testcases successfully"
        )));
    }
}

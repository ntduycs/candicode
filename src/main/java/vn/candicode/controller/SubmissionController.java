package vn.candicode.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewCodeRunRequest;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.SubmissionHistory;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.SubmissionService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class SubmissionController extends Controller {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    protected String getPath() {
        return "submissions";
    }

    @PostMapping(path = "challenges/{id}/submissions")
    public ResponseEntity<?> saveSubmission(@PathVariable("id") Long challengeId, @RequestBody @Valid NewSubmissionRequest payload, @CurrentUser UserPrincipal me) {
        submissionService.saveSubmission(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Saved submission successfully"
        )));
    }

    @GetMapping(path = "submissions/me")
    public ResponseEntity<?> mySubmissions(@ModelAttribute PaginatedRequest payload, @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(payload.getPage(), payload.getSize(), payload.getSort(), payload.getDirection());

        PaginatedResponse<SubmissionHistory> mySubmissions = submissionService.getMySubmissionHistory(pageable, me);

        return ResponseEntity.ok(ResponseFactory.build(mySubmissions));
    }

    @GetMapping(path = "challenges/{id}/submissions")
    public ResponseEntity<?> getChallengeSubmissionList(@ModelAttribute PaginatedRequest payload, @PathVariable("id") Long challengeId) {
        Pageable pageable = getPaginationConfig(payload);

        PaginatedResponse<SubmissionHistory> challengeSubmissions = submissionService.getSubmissionsByChallenge(pageable, challengeId);

        return ResponseEntity.ok(ResponseFactory.build(challengeSubmissions));
    }

    @GetMapping(path = "contests/{cid}/rounds/{rid}/submissions")
    public ResponseEntity<?> getContestSubmissionsByRound(@ModelAttribute PaginatedRequest payload, @PathVariable("cid") Long contestId, @PathVariable("rid") Long roundId, @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(payload);

        PaginatedResponse<SubmissionHistory> roundSubmissions = submissionService.getSubmissionsByContestRound(pageable, roundId);

        return ResponseEntity.ok(ResponseFactory.build(roundSubmissions));
    }

    @PostMapping(path = "challenges/{id}/score")
    public ResponseEntity<?> doScore(@PathVariable("id") Long challengeId, @RequestBody @Valid NewCodeRunRequest payload, @CurrentUser UserPrincipal me) {
        SubmissionSummary submissionSummary = submissionService.doScoreSubmission(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(submissionSummary));
    }
}

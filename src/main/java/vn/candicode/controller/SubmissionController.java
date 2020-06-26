package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewSubmissionRequest;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.SubmissionService;

import javax.validation.Valid;

@RestController
public class SubmissionController extends Controller {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    protected String getPath() {
        return null;
    }

    @PostMapping(path = "challenges/{id}/submissions")
    public ResponseEntity<?> submitCode(@PathVariable("id") Long challengeId, @RequestBody @Valid NewSubmissionRequest payload, @CurrentUser UserPrincipal me) {
        SubmissionSummary summary = submissionService.doScoreSubmission(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(summary));
    }
}

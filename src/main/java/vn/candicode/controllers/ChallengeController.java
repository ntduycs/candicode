package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.requests.TestcasesRequest;
import vn.candicode.payloads.responses.*;
import vn.candicode.payloads.validators.FileTypeAcceptable;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;

import javax.validation.Valid;
import java.util.Map;

import static vn.candicode.common.filesystem.FileType.RAR;
import static vn.candicode.common.filesystem.FileType.ZIP;

@RestController
@Log4j2
public class ChallengeController extends GenericController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping(path = "/challenges/{id}")
    public ResponseEntity<?> getChallengeDetails(@PathVariable("id") Long challengeId) {
        ChallengeDetails challengeDetails = challengeService.getChallengeDetails(challengeId);

        return ResponseEntity.ok(GenericResponse.from(challengeDetails));
    }

    @GetMapping(path = "/challenges")
    public ResponseEntity<?> getChallengeList(@RequestParam(name = "page", defaultValue = "1") int page,
                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                              @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                              @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<ChallengeSummary> items = challengeService.getChallengeList(pageable);

        return ResponseEntity.ok(GenericResponse.from(items));
    }

    @PostMapping(path = "/challenges")
    public ResponseEntity<?> createChallenge(@Valid @ModelAttribute NewChallengeRequest payload, @CurrentUser UserPrincipal currentUser) {
        Long challengeId = challengeService.createChallenge(payload, currentUser);

        return ResponseEntity.created(getResourcePath(challengeId)).body(GenericResponse.from(
            Map.of("message", "Created new challenge successfully"), HttpStatus.CREATED
        ));
    }

    @PostMapping(path = "/challenges/source")
    public ResponseEntity<?> uploadChallengeSourceCode(@RequestParam("source") @FileTypeAcceptable({ZIP, RAR}) MultipartFile source,
                                                       @CurrentUser UserPrincipal currentUser) {
        SourceCodeStructure payload = challengeService.storeChallengeSourceCode(source, currentUser);

        return ResponseEntity.ok(GenericResponse.from(
            payload, HttpStatus.OK
        ));
    }

    @PostMapping(path = "/challenges/{id}/testcases")
    public ResponseEntity<?> addTestcases(@PathVariable("id") Long challengeId,
                                          @RequestBody @Valid TestcasesRequest payload,
                                          @CurrentUser UserPrincipal currentUser) {
        Integer numCreatedTestcases = challengeService.createTestcases(challengeId, payload, currentUser);

        return ResponseEntity.ok(GenericResponse.from(Map.of(
            "message", "Created " + numCreatedTestcases + " newly testcases successfully"
        )));
    }

    @PostMapping(path = "/challenges/{id}/submissions")
    public ResponseEntity<?> submitCode(@PathVariable("id") Long challengeId,
                                        @RequestBody @Valid SubmissionRequest payload,
                                        @CurrentUser UserPrincipal currentUser) {
        SubmissionResult result = challengeService.evaluateSubmission(challengeId, payload, currentUser);

        return ResponseEntity.ok(GenericResponse.from(result));
    }

    @Override
    protected String getResourceBasePath() {
        return "challenges";
    }
}

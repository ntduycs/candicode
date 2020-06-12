package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.*;
import vn.candicode.payloads.responses.*;
import vn.candicode.payloads.validators.FileTypeAcceptable;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

    @GetMapping(path = "/challenges/me")
    public ResponseEntity<?> getMyChallengesList(@RequestParam(name = "page", defaultValue = "1") int page,
                                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                                 @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                                 @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                                 @CurrentUser UserPrincipal currentUser) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<ChallengeSummary> items = challengeService.getMyChallengeList(pageable, currentUser);

        return ResponseEntity.ok(GenericResponse.from(items));
    }

    @PostMapping(path = "/challenges/{id}")
    public ResponseEntity<?> editChallenge(@PathVariable("id") Long challengeId,
                                           @Valid @ModelAttribute EditChallengeRequest payload) {
        challengeService.editChallenge(challengeId, payload);

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", "Edited challenge successfully"), HttpStatus.OK
        ));
    }

    @DeleteMapping(path = "/challenges/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable("id") Long challengeId) {
        challengeService.deleteChallenge(challengeId);

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", "Delete challenge successfully"), HttpStatus.OK
        ));
    }

    @PostMapping(path = "/challenges")
    public ResponseEntity<?> createChallenge(@Valid @ModelAttribute NewChallengeRequest payload,
                                             @CurrentUser UserPrincipal currentUser) {
        Long challengeId = challengeService.createChallenge(payload, currentUser);

        return ResponseEntity.created(getResourcePath(challengeId)).body(GenericResponse.from(
            Map.of("challengeId", challengeId,
                "message", "Created new challenge successfully"), HttpStatus.CREATED
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

    @PostMapping(path = "challenges/{id}/testcases/verification")
    public ResponseEntity<?> verifyTestcase(@PathVariable("id") Long challengeId,
                                            @RequestBody @Valid TestcaseVerificationRequest payload) {
        TestcaseVerificationResult result = challengeService.verifyTestcase(challengeId, payload);

        return ResponseEntity.ok(GenericResponse.from(result));
    }

    @DeleteMapping(path = "/challenges/{id}/testcases")
    public ResponseEntity<?> removeTestcase(@PathVariable("id") Long challengeId,
                                            @RequestBody @Valid RemoveTestcasesRequest payload) {
        RemoveTestcasesResult result = challengeService.removeTestcases(challengeId, payload.getTestcaseIds());

        return ResponseEntity.ok(GenericResponse.from(result));
    }

    @PutMapping(path = "/challenges/{id}/testcases")
    public ResponseEntity<?> updateTestcases(@PathVariable("id") Long challengeId,
                                             @RequestBody @Valid UpdateTestcasesRequest payload) {
        int numUpdatedTestcases = challengeService.updateTestcases(challengeId, payload);

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", "Updated " + numUpdatedTestcases + " testcases successfully")
        ));
    }

    @PostMapping(path = "/challenges/{id}/languages")
    public ResponseEntity<?> addLanguage(@PathVariable("id") Long challengeId,
                                         @RequestBody @Valid NewLanguageRequest payload,
                                         @CurrentUser UserPrincipal currentUser) {
        boolean hasError = challengeService.addLanguage(challengeId, payload, currentUser);

        String message = (hasError ? "Failed " : "Success ") + "to add " + payload.getLanguage() + " to challenge successfully";

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", message)
        ));
    }

    @DeleteMapping(path = "/challenges/{id}/languages")
    public ResponseEntity<?> removeLanguage(@PathVariable("id") Long challengeId,
                                            @RequestBody @NotBlank(message = "Field 'language' is required but not be given") String language) {
        boolean hasError = challengeService.removeLanguage(challengeId, language);

        String message = (hasError ? "Failed " : "Success ") + "to remove " + language + " from challenge successfully";

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", message)
        ));
    }

    @Override
    protected String getResourceBasePath() {
        return "challenges";
    }
}

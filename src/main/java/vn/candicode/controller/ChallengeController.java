package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.ChallengePaginatedRequest;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.request.NewChallengeSourceRequest;
import vn.candicode.payload.request.UpdateChallengeRequest;
import vn.candicode.payload.response.ChallengeDetails;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.sub.Leader;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ChallengeService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INSUFFICIENT_STORAGE;

@RestController
@CrossOrigin(origins = {"client-origin"})
public class ChallengeController extends Controller {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    protected String getPath() {
        return "challenges";
    }

    @PostMapping(path = "challenges/source", produces = {"application/json"})
    public ResponseEntity<?> uploadChallengeSource(@ModelAttribute @Valid NewChallengeSourceRequest payload,
                                                   @CurrentUser UserPrincipal author) {
        DirectoryTree tree = challengeService.storeChallengeSource(payload.getSource(), author);

        return ResponseEntity.ok(ResponseFactory.build(tree));
    }

    @GetMapping(path = "challenges/{id}/leaderboard")
    public ResponseEntity<?> getChallengeLeaderboard(@PathVariable("id") Long challengeId) {
        List<Leader> leaders = challengeService.getChallengeLeaders(challengeId);

        return ResponseEntity.ok(ResponseFactory.build(leaders));
    }

    @PostMapping(path = "challenges", produces = {"application/json"})
    public ResponseEntity<?> createChallenge(@ModelAttribute @Valid NewChallengeRequest payload, @CurrentUser UserPrincipal author) {
        Map<String, Object> result = challengeService.createChallenge(payload, author);

        return ResponseEntity.created(getResourcePath((Long) result.get("challengeId"))).body(ResponseFactory.build(
            Map.of(
                "message", "Created new challenge successfully",
                "challengeId", result.get("challengeId"),
                "errors", result.getOrDefault("errors", new ArrayList<>())
            )
        ));
    }

    @GetMapping(path = "challenges", produces = {"application/json"})
    public ResponseEntity<?> getChallengeList(@ModelAttribute ChallengePaginatedRequest payload) {
        PaginatedResponse<ChallengeSummary> items = challengeService.getChallengeList(payload);

        return ResponseEntity.ok(ResponseFactory.build(items));
    }

    @GetMapping(path = "challenges/me")
    public ResponseEntity<?> getMyChallengeList(@ModelAttribute ChallengePaginatedRequest payload, @CurrentUser UserPrincipal me) {
        PaginatedResponse<ChallengeSummary> items = challengeService.getMyChallengeList(payload, me.getUserId());

        return ResponseEntity.ok(ResponseFactory.build(items));
    }

    @GetMapping(path = "challenges/{id}")
    public ResponseEntity<?> getChallengeDetails(@PathVariable("id") Long challengeId, @CurrentUser UserPrincipal me) {
        ChallengeDetails challengeDetails = challengeService.getChallengeDetails(challengeId, me);

        return ResponseEntity.ok(ResponseFactory.build(challengeDetails));
    }

    @PostMapping(path = "challenges/{id}")
    public ResponseEntity<?> updateChallenge(@PathVariable("id") Long challengeId, @ModelAttribute @Valid UpdateChallengeRequest payload, @CurrentUser UserPrincipal me) {
        Map<String, Object> response = challengeService.updateChallenge(challengeId, payload, me);

        String message;

        boolean isSuccess = (boolean) response.getOrDefault("success", false);

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) response.getOrDefault("errors", new ArrayList<>());

        if (!isSuccess) {
            message = "Failed to update challenge";
            return new ResponseEntity<>(ResponseFactory.build(Map.of(
                "message", message, "errors", errors)
            ), INSUFFICIENT_STORAGE);
        }

        if (errors.size() > 0) {
            message = "Updated challenge partially";
        } else {
            message = "Updated challenge successfully";
        }

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", message,
            "errors", errors
        )));
    }

    @DeleteMapping(path = "challenges/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable("id") Long challengeId, @CurrentUser UserPrincipal me) {
        challengeService.deleteChallenge(challengeId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Deleted challenge successfully"
        )));
    }

}

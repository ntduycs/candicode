package vn.candicode.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ChallengeService;

import javax.validation.Valid;
import java.util.Map;

@RestController
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
    public ResponseEntity<?> uploadChallengeSource(@RequestParam("source") MultipartFile file, @CurrentUser UserPrincipal author) {
        DirectoryTree tree = challengeService.storeChallengeSource(file, author);

        return ResponseEntity.ok(ResponseFactory.build(tree));
    }

    @PostMapping(path = "challenges", produces = {"application/json"})
    public ResponseEntity<?> createChallenge(@ModelAttribute NewChallengeRequest payload, @CurrentUser UserPrincipal author) {
        Long challengeId = challengeService.createChallenge(payload, author);

        return ResponseEntity.created(getResourcePath(challengeId)).body(ResponseFactory.build(
            Map.of(
                "message", "Created new challenge successfully",
                "challengeId", challengeId
            )
        ));
    }

    @GetMapping(path = "challenges", produces = {"application/json"})
    public ResponseEntity<?> getChallengeList(@RequestBody @Valid PaginatedRequest payload) {
        Pageable pageable = getPaginationConfig(payload.getPage(), payload.getSize(), payload.getSort(), payload.getDirection());

        PaginatedResponse<ChallengeSummary> items = challengeService.getChallengeList(pageable);

        return ResponseEntity.ok(ResponseFactory.build(items));
    }

}

package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.response.DirectoryTree;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ChallengeService;

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

}

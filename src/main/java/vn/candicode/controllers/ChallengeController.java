package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.NewChallengeRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.ChallengeService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Log4j2
public class ChallengeController extends GenericController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping(path = "/challenges")
    public ResponseEntity<?> createChallenge(@Valid @ModelAttribute NewChallengeRequest payload, @CurrentUser UserPrincipal currentUser) {
        Long challengeId = challengeService.createChallenge(payload, currentUser);

        return ResponseEntity.created(getResourcePath(challengeId)).body(GenericResponse.from(
            Map.of("message", "Created new challenge successfully"), HttpStatus.CREATED
        ));
    }

    @PostMapping(path = "/challenges/source")
    public ResponseEntity<?> uploadChallengeSourceCode(@RequestParam("source") MultipartFile source, @CurrentUser UserPrincipal currentUser) {

    }

    @Override
    protected String getResourceBasePath() {
        return "challenges";
    }
}

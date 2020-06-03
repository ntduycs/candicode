package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.services.ChallengeService;

@RestController
@Log4j2
public class ChallengeController extends GenericController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping(path = "/challenges")
    public ResponseEntity<?> createChallenge() {
        return null;
    }

    @Override
    protected String getResourceBasePath() {
        return "challenges";
    }
}

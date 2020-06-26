package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewChallengeConfigurationRequest;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ChallengeConfigurationService;

import javax.validation.Valid;

@RestController
public class ChallengeConfigurationController extends Controller {
    private final ChallengeConfigurationService challengeConfigurationService;

    public ChallengeConfigurationController(ChallengeConfigurationService challengeConfigurationService) {
        this.challengeConfigurationService = challengeConfigurationService;
    }

    @Override
    protected String getPath() {
        return "challenge-configurations";
    }

    @PostMapping(path = "challenges/{id}/languages")
    public ResponseEntity<?> addSupportedLanguage(@PathVariable("id") Long challengeId, @RequestBody @Valid NewChallengeConfigurationRequest payload, @CurrentUser UserPrincipal me) {
        SubmissionSummary summary = challengeConfigurationService.addSupportedLanguage(challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(summary));
    }
}

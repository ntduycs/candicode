package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewChallengeConfigurationRequest;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ChallengeConfigurationService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

    @DeleteMapping(path = "challenges/{id}/languages")
    public ResponseEntity<?> removeSupportedLanguage(@PathVariable("id") Long challengeId, @RequestBody @NotBlank(message = "Field 'language' is required but not be given") String language, @CurrentUser UserPrincipal me) {
        boolean success = challengeConfigurationService.removeSupportedLanguage(challengeId, language);

        if (success) {
            return ResponseEntity.ok(ResponseFactory.build(Map.of(
                "message", "Remove language configuration successfully"
            )));
        } else {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Unknown error! Cannot remove language"
            ));
        }
    }
}

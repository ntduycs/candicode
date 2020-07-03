package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ContestRegistrationService;

import java.util.Map;

@RestController
public class ContestRegistrationController extends Controller {
    private final ContestRegistrationService contestRegistrationService;

    public ContestRegistrationController(ContestRegistrationService contestRegistrationService) {
        this.contestRegistrationService = contestRegistrationService;
    }

    @Override
    protected String getPath() {
        return "contest-registrations";
    }

    @PostMapping(path = "contests/{id}/registration")
    public ResponseEntity<?> enroll(@PathVariable("id") Long contestId, @CurrentUser UserPrincipal me) {
        contestRegistrationService.enrollContest(contestId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Enrolled successfully"
        )));
    }
}

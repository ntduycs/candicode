package vn.candicode.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.services.ChallengeService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/challenges")
public class ChallengeController extends BaseController {
    private final ChallengeService service;

    public ChallengeController(ChallengeService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "challenges";
    }

    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createChallenge(@RequestBody @Valid ChallengeRequest request) {
        return null;
    }
}

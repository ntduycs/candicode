package vn.candicode.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.commons.rest.RestResponse;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.responses.SubmissionResult;
import vn.candicode.security.CurrentUser;
import vn.candicode.services.SubmissionService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/submissions")
public class SubmissionController extends BaseController {
    private final SubmissionService service;

    @Autowired
    public SubmissionController(SubmissionService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "submissions";
    }

    @PostMapping(path = "")
    public ResponseEntity<?> submitCode(@RequestBody @Valid SubmissionRequest request,
                                        @CurrentUser User user) {
        SubmissionResult submissionResult = service.check(request, user);

        return ResponseEntity.ok(RestResponse.build(submissionResult, HttpStatus.OK));
    }
}

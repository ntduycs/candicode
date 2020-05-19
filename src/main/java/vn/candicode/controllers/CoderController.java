package vn.candicode.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payloads.requests.RegisterRequest;
import vn.candicode.services.CoderService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/coders")
public class CoderController extends BaseController {
    private final CoderService service;

    public CoderController(CoderService service) {
        this.service = service;
    }

    @Override
    protected String getPath() {
        return "coders";
    }

    @PostMapping(path = "")
    public ResponseEntity<?> createNewCoderAccount(@RequestBody @Valid RegisterRequest registerRequest) {
        Long resourceId = service.createNewCoderAccount(registerRequest);

        return ResponseEntity.created(location(resourceId)).build();
    }
}

package vn.candicode.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.rest.RestResponse;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeDetail;
import vn.candicode.payloads.validators.MultipartRequestValidator;
import vn.candicode.repositories.ChallengeRepository;
import vn.candicode.security.CurrentUser;
import vn.candicode.services.ChallengeService;

@RestController
@RequestMapping(path = "/challenges")
public class ChallengeController extends BaseController {
    private final ChallengeService service;

    private final MultipartRequestValidator validator;

    private final StorageLocation storageLocation;

    private final ChallengeRepository challengeRepository;

    public ChallengeController(ChallengeService service, MultipartRequestValidator validator, StorageLocation storageLocation, ChallengeRepository challengeRepository) {
        this.service = service;
        this.validator = validator;
        this.storageLocation = storageLocation;
        this.challengeRepository = challengeRepository;
    }

    @Override
    protected String getPath() {
        return "challenges";
    }

    @PostMapping(path = "")
    public ResponseEntity<?> createChallenge(@ModelAttribute ChallengeRequest request, BindingResult bindingResult, @CurrentUser User user) throws BindException {
        validator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Long challengeId = service.createChallenge(request, user);

        return ResponseEntity.created(location(challengeId)).build();
    }

    @PostMapping(path = "source-upload")
    public ResponseEntity<?> parseDirTree(@RequestParam("sourceCode") MultipartFile sourceCode, @CurrentUser User user) {
        Component dirTree = service.parseDirTree(sourceCode, user);

        return ResponseEntity.ok(RestResponse.build(dirTree, HttpStatus.OK));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getChallengeById(@PathVariable("id") Long id) {
//        ChallengeDetail challengeDetail = service.
        return null;
    }
}

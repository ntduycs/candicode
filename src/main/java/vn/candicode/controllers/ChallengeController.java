package vn.candicode.controllers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.rest.RestResponse;
import vn.candicode.models.User;
import vn.candicode.payloads.requests.ChallengeMetadataRequest;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.requests.TestcaseRequest;
import vn.candicode.payloads.responses.ChallengeDetail;
import vn.candicode.security.CurrentUser;
import vn.candicode.services.ChallengeService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/challenges")
public class ChallengeController extends BaseController {
    private final ChallengeService service;

    private final Validator challengeRequestValidator;

    private final Validator challengeMetadataRequestValidator;

    public ChallengeController(ChallengeService service, @Qualifier("challenge") Validator challengeRequestValidator,
                               @Qualifier("challengeMetadata") Validator challengeMetadataRequestValidator) {
        this.service = service;
        this.challengeRequestValidator = challengeRequestValidator;
        this.challengeMetadataRequestValidator = challengeMetadataRequestValidator;
    }

    @Override
    protected String getPath() {
        return "challenges";
    }

    @PostMapping(path = "")
    public ResponseEntity<?> createChallenge(@ModelAttribute ChallengeRequest request, BindingResult bindingResult, @CurrentUser User user) throws BindException {
        challengeRequestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Long challengeId = service.createChallenge(request, user);

        return ResponseEntity.created(location(challengeId)).body(RestResponse.build(challengeId, HttpStatus.CREATED));
    }

    @PostMapping(path = "source-upload")
    public ResponseEntity<?> parseDirTree(@RequestParam("sourceCode") MultipartFile sourceCode, @CurrentUser User user) {
        Component dirTree = service.parseDirTree(sourceCode, user);

        return ResponseEntity.ok(RestResponse.build(dirTree, HttpStatus.OK));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getChallengeById(@PathVariable("id") Long id) {
        ChallengeDetail challengeDetail = service.getChallengeById(id);
        return ResponseEntity.ok(RestResponse.build(challengeDetail, HttpStatus.OK));
    }

    @GetMapping(path = "")
    public ResponseEntity<?> getChallenges(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int perPage,
                                           @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                           @RequestParam(name = "direction", defaultValue = "desc") Sort.Direction direction) {
        Sort sortingConfig = sortBy != null && direction != null
            ? Sort.by(direction, sortBy)
            : Sort.unsorted();

        Pageable pageable = PageRequest.of(page, perPage, sortingConfig);

        Map<String, Object> container = service.getChallenges(pageable);

        return ResponseEntity.ok(RestResponse.build(container, HttpStatus.OK));
    }

    @PostMapping(path = "/{id}")
    public ResponseEntity<?> editChallengeMetadata(
        @ModelAttribute ChallengeMetadataRequest request,
        BindingResult bindingResult,
        @PathVariable("id") Long id,
        @CurrentUser User user
    ) throws BindException {
        challengeMetadataRequestValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Long challengeId = service.updateChallengeMetadata(id, request, user);

        return ResponseEntity.ok(RestResponse.build(challengeId, HttpStatus.OK));
    }

    @PostMapping("/{id}/testcases")
    public ResponseEntity<?> updateTestcases(@PathVariable("id") Long id,
                                             @RequestBody @Valid TestcaseRequest request,
                                             @CurrentUser User user) {
        Map<String, Object> container = service.adjustTestcases(id, request, user);

        return ResponseEntity.ok(RestResponse.build(container, HttpStatus.OK));
    }

    @PostMapping("/{id}/configs")
    public ResponseEntity<?> updateLanguageConfig(@PathVariable("id") Long id) {
        return null;
    }
}

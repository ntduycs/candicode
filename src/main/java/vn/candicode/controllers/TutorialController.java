package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.TutorialRequest;
import vn.candicode.payloads.responses.PaginatedResponse;
import vn.candicode.payloads.responses.TutorialDetails;
import vn.candicode.payloads.responses.TutorialSummary;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.v2.TutorialService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Log4j2
public class TutorialController extends GenericController {
    private final TutorialService tutorialService;

    public TutorialController(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    @PostMapping(path = "/tutorials")
    public ResponseEntity<?> createTutorial(@ModelAttribute @Valid TutorialRequest payload,
                                            @CurrentUser UserPrincipal userPrincipal) {
        Long tutorialId = tutorialService.createTutorial(payload, userPrincipal);

        return ResponseEntity.created(getResourcePath(tutorialId)).body(GenericResponse.from(
            Map.of("tutorialId", tutorialId,
                "message", "Created new tutorial successfully"), HttpStatus.CREATED
        ));
    }

    @GetMapping(path = "/tutorials/{id}")
    public ResponseEntity<?> getTutorialDetails(@PathVariable("id") Long tutorialId) {
        TutorialDetails response = tutorialService.getTutorialDetails(tutorialId);

        return ResponseEntity.ok(GenericResponse.from(response));
    }

    @GetMapping(path = "/tutorials")
    public ResponseEntity<?> getTutorialList(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                             @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                             @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<TutorialSummary> items = tutorialService.getTutorialList(pageable);

        return ResponseEntity.ok(GenericResponse.from(items));
    }

    @GetMapping(path = "/tutorials/me")
    public ResponseEntity<?> getMyTutorials(@RequestParam(name = "page", defaultValue = "1") int page,
                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                            @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                            @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                            @CurrentUser UserPrincipal user) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<TutorialSummary> items = tutorialService.getMyTutorialList(pageable, user);

        return ResponseEntity.ok(GenericResponse.from(items));
    }


    @DeleteMapping(path = "/tutorials/{id}")
    public ResponseEntity<?> deleteTutorial(@PathVariable("id") Long tutorialId) {
        tutorialService.deleteTutorial(tutorialId);

        return ResponseEntity.ok(GenericResponse.from(
            Map.of("message", "Delete tutorial successfully"), HttpStatus.OK
        ));
    }

    @Override
    protected String getResourceBasePath() {
        return "tutorials";
    }
}

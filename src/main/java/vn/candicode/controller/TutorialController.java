package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewTutorialRequest;
import vn.candicode.payload.request.UpdateTutorialRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.TutorialService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class TutorialController extends Controller {
    private final TutorialService tutorialService;

    public TutorialController(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    @Override
    protected String getPath() {
        return "tutorials";
    }

    @PostMapping(path = "tutorials")
    public ResponseEntity<?> createTutorial(@ModelAttribute @Valid NewTutorialRequest payload, @CurrentUser UserPrincipal me) {
        Long tutorialId = tutorialService.createTutorial(payload, me);

        return ResponseEntity.created(getResourcePath(tutorialId)).body(ResponseFactory.build(Map.of(
            "message", "Created tutorial successfully"
        )));
    }

    @PostMapping(path = "tutorials/{id}")
    public ResponseEntity<?> updateTutorial(@PathVariable("id") Long tutorialId, @ModelAttribute UpdateTutorialRequest payload, @CurrentUser UserPrincipal me) {
        tutorialService.updateTutorial(tutorialId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated tutorial successfully"
        )));
    }

    /**
     * TODO: Optimize SQL
     *
     * @param tutorialId
     * @param me
     * @return
     */
    @DeleteMapping(path = "tutorials/{id}")
    public ResponseEntity<?> removeTutorial(@PathVariable("id") Long tutorialId, @CurrentUser UserPrincipal me) {
        tutorialService.removeTutorial(tutorialId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Deleted tutorial successfully"
        )));
    }
}

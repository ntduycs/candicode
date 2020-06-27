package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
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
}

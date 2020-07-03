package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.ReactionRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ReactionService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class ReactionController extends Controller {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @Override
    protected String getPath() {
        return "reactions";
    }

    @PostMapping(path = "reaction")
    public ResponseEntity<?> react(@RequestBody @Valid ReactionRequest payload, @CurrentUser UserPrincipal me) {
        reactionService.storeReaction(payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Success"
        )));
    }
}

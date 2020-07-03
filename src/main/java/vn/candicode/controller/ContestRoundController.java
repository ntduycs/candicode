package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewRoundListRequest;
import vn.candicode.payload.request.RemoveRoundListRequest;
import vn.candicode.payload.request.UpdateRoundListRequest;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ContestRoundService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class ContestRoundController extends Controller {
    private final ContestRoundService contestRoundService;

    public ContestRoundController(ContestRoundService contestRoundService) {
        this.contestRoundService = contestRoundService;
    }

    @Override
    protected String getPath() {
        return "contest-rounds";
    }

    @PostMapping(path = "contests/{id}/rounds")
    public ResponseEntity<?> createRound(@PathVariable("id") Long contestId, @RequestBody @Valid NewRoundListRequest payload, @CurrentUser UserPrincipal me) {
        contestRoundService.createRounds(contestId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Created new round(s) successfully"
        )));
    }

    @PutMapping(path = "contests/{cid}/rounds")
    public ResponseEntity<?> updateRound(@PathVariable("cid") Long contestId, @RequestBody @Valid UpdateRoundListRequest payload, @CurrentUser UserPrincipal me) {
        contestRoundService.updateRound(contestId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated round successfully"
        )));
    }

    @DeleteMapping(path = "contests/{cid}/rounds")
    public ResponseEntity<?> removeRound(@PathVariable("cid") Long contestId, @RequestBody @Valid RemoveRoundListRequest payload, @CurrentUser UserPrincipal me) {
        contestRoundService.removeRound(contestId, payload.getRoundIds(), me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Removed round successfully"
        )));
    }
}

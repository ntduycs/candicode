package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewRoundRequest;
import vn.candicode.payload.request.UpdateRoundRequest;
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
    public ResponseEntity<?> createRound(@PathVariable("id") Long contestId, @RequestBody @Valid NewRoundRequest payload, @CurrentUser UserPrincipal me) {
        Long roundId = contestRoundService.createRound(contestId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Created new round successfully",
            "roundId", roundId
        )));
    }

    @PutMapping(path = "contests/{cid}/rounds/{rid}")
    public ResponseEntity<?> updateRound(@PathVariable("cid") Long contestId, @PathVariable("rid") Long roundId, @RequestBody @Valid UpdateRoundRequest payload, @CurrentUser UserPrincipal me) {
        contestRoundService.updateRound(roundId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated round successfully"
        )));
    }

    @DeleteMapping(path = "contests/{cid}/rounds/{rid}")
    public ResponseEntity<?> removeRound(@PathVariable("cid") Long contestId, @PathVariable("rid") Long roundId, @CurrentUser UserPrincipal me) {
        contestRoundService.removeRound(roundId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Removed round successfully"
        )));
    }
}

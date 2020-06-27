package vn.candicode.controller;

import org.springframework.web.bind.annotation.RestController;
import vn.candicode.service.ContestRoundService;

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

//    @PostMapping(path = "contests/{id}/rounds")
//    public ResponseEntity<?> createRound(@PathVariable)
}

package vn.candicode.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.NewContestRequest;
import vn.candicode.payloads.requests.NewRoundsRequest;
import vn.candicode.payloads.requests.UpdateContestRequest;
import vn.candicode.payloads.responses.ContestDetails;
import vn.candicode.payloads.responses.ContestSummary;
import vn.candicode.payloads.responses.LeaderBoard;
import vn.candicode.payloads.responses.PaginatedResponse;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.v2.ContestService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class ContestController extends GenericController {
    @Override
    protected String getResourceBasePath() {
        return "contests";
    }

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    /**
     * CREATE NEW CONTEST
     *
     * @param payload
     * @param author
     * @return
     */
    @PostMapping(path = "contests", produces = {"application/json"})
    public ResponseEntity<?> createContest(@ModelAttribute NewContestRequest payload, @CurrentUser UserPrincipal author) {
        Long contestId = contestService.createContest(payload, author);

        return ResponseEntity.created(getResourcePath(contestId)).body(GenericResponse.from(
            Map.of(
                "message", "Created new contest successfully",
                "contestId", contestId
            )
        ));
    }

    /**
     * UPDATE EXISTING CONTEST
     *
     * @param contestId
     * @param payload
     * @param author
     * @return
     */
    @PostMapping(path = "contests/{id}", produces = {"application/json"})
    public ResponseEntity<?> updateContest(@PathVariable("id") Long contestId, @ModelAttribute UpdateContestRequest payload, @CurrentUser UserPrincipal author) {
        contestService.updateContest(contestId, payload, author);

        return ResponseEntity.ok(GenericResponse.from(
            Map.of(
                "message", "Updated challenge successfully"
            )
        ));
    }

    @GetMapping(path = "contests", produces = {"application/json"})
    public ResponseEntity<?> getContestLists(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                             @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                             @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<ContestSummary> summaryPaginatedResponse = contestService.getContestList(pageable);


        return ResponseEntity.ok(GenericResponse.from(summaryPaginatedResponse));
    }

    @GetMapping(path = "contests/me", produces = {"application/json"})
    public ResponseEntity<?> getContestLists(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                             @RequestParam(name = "sort", defaultValue = "createdAt") String sortBy,
                                             @RequestParam(name = "direction", defaultValue = "desc") String direction,
                                             @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(page, size, sortBy, direction);

        PaginatedResponse<ContestSummary> summaryPaginatedResponse = contestService.getMyContestList(pageable, me);


        return ResponseEntity.ok(GenericResponse.from(summaryPaginatedResponse));
    }

    @GetMapping(path = "contests/{id}")
    public ResponseEntity<?> getContestDetails(@PathVariable("id") Long contestId, @CurrentUser UserPrincipal me) {
        ContestDetails details = contestService.getContestDetails(contestId, me);

        return ResponseEntity.ok(details);
    }

    @GetMapping(path = "contests/{id}/leaders")
    public ResponseEntity<?> getLeaderBoard(@PathVariable("id") Long contestId, @CurrentUser UserPrincipal me) {
        LeaderBoard leaderBoard;

        return null;
    }

    @PostMapping(path = "contest/{id}/rounds")
    public ResponseEntity<?> createRound(@PathVariable("id") Long contestId, @RequestBody @Valid NewRoundsRequest payload, @CurrentUser UserPrincipal me) {
        contestService.createRound(contestId, payload, me);


        return ResponseEntity.created(getResourcePath(contestId)).body(GenericResponse.from(
            Map.of("message", "Create rounds for contest successfully")
        ));
    }
}

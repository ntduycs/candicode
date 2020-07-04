package vn.candicode.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewContestRequest;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.request.UpdateContestRequest;
import vn.candicode.payload.response.ContestDetails;
import vn.candicode.payload.response.ContestSummary;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.ContestService;

import java.util.Map;

@RestController
public class ContestController extends Controller {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @Override
    protected String getPath() {
        return "contests";
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

        return ResponseEntity.created(getResourcePath(contestId)).body(ResponseFactory.build(
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

        return ResponseEntity.ok(ResponseFactory.build(
            Map.of(
                "message", "Updated challenge successfully"
            )
        ));
    }

    @DeleteMapping(path = "contests/{id}")
    public ResponseEntity<?> removeContest(@PathVariable("id") Long contestId, @CurrentUser UserPrincipal me) {
        contestService.removeContest(contestId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Removed contest successfully"
        )));
    }

    @GetMapping(path = "contests", produces = {"application/json"})
    public ResponseEntity<?> getContestLists(@ModelAttribute PaginatedRequest payload) {
        Pageable pageable = getPaginationConfig(payload.getPage(), payload.getSize(), payload.getSort(), payload.getDirection());

        PaginatedResponse<ContestSummary> summaries = contestService.getContestList(pageable);

        return ResponseEntity.ok(ResponseFactory.build(summaries));
    }

    @GetMapping(path = "contests/me", produces = {"application/json"})
    public ResponseEntity<?> getMyContestLists(@ModelAttribute PaginatedRequest payload, @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(payload.getPage(), payload.getSize(), payload.getSort(), payload.getDirection());

        PaginatedResponse<ContestSummary> summaries = contestService.getMyContestList(pageable, me.getUserId());

        return ResponseEntity.ok(ResponseFactory.build(summaries));
    }

    @GetMapping(path = "contests/{id}")
    public ResponseEntity<?> getContestDetails(@PathVariable("id") Long contestId) {
        ContestDetails details = contestService.getContestDetails(contestId);

        return ResponseEntity.ok(ResponseFactory.build(details));
    }
}

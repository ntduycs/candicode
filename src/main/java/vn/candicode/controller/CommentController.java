package vn.candicode.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.request.UpdateCommentRequest;
import vn.candicode.payload.response.Comment;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommentService;

import javax.validation.Valid;
import java.util.Map;

import static vn.candicode.common.CommentSubject.CHALLENGE;
import static vn.candicode.common.CommentSubject.TUTORIAL;

@RestController
public class CommentController extends Controller {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    protected String getPath() {
        return "comments";
    }

    @PostMapping(path = "challenges/{id}/comments")
    public ResponseEntity<?> addChallengeComment(@PathVariable("id") Long challengeId, @RequestBody @Valid NewCommentRequest payload, @CurrentUser UserPrincipal me) {
        Comment comment = commentService.addComment(CHALLENGE, challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PostMapping(path = "tutorials/{id}/comments")
    public ResponseEntity<?> addTutorialComment(@PathVariable("id") Long tutorialId, @RequestBody @Valid NewCommentRequest payload, @CurrentUser UserPrincipal me) {
        Comment comment = commentService.addComment(TUTORIAL, tutorialId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PutMapping(path = "challenges/{clid}/comments/{cid}")
    public ResponseEntity<?> updateChallengeComment(@PathVariable("clid") Long challengeId, @PathVariable("cid") Long commentId, @RequestBody @Valid UpdateCommentRequest payload, @CurrentUser UserPrincipal me) {
        Comment comment = commentService.updateComment(challengeId, CHALLENGE, commentId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PutMapping(path = "tutorials/{tid}/comments/{cid}")
    public ResponseEntity<?> updateTutorialComment(@PathVariable("tid") Long tutorialId, @PathVariable("cid") Long commentId, @RequestBody @Valid UpdateCommentRequest payload, @CurrentUser UserPrincipal me) {
        Comment comment = commentService.updateComment(tutorialId, TUTORIAL, commentId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @DeleteMapping(path = "challenges/{clid}/comments/{cid}")
    public ResponseEntity<?> deleteChallengeComment(@PathVariable("clid") Long challengeId, @PathVariable("cid") Long commentId, @CurrentUser UserPrincipal me) {
        commentService.deleteComment(challengeId, commentId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Deleted comment successfully"
        )));
    }

    @DeleteMapping(path = "tutorials/{tid}/comments/{cid}")
    public ResponseEntity<?> deleteTutorialComment(@PathVariable("tid") Long tutorialId, @PathVariable("cid") Long commentId, @CurrentUser UserPrincipal me) {
        commentService.deleteComment(tutorialId, commentId, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Deleted comment successfully"
        )));
    }

    @GetMapping(path = "challenges/{cid}/comments")
    public ResponseEntity<?> getChallengeCommentList(@PathVariable("cid") Long challengeId, @RequestParam(value = "size", defaultValue = "10") int size, @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(1, size, "createdAt", "desc");

        PaginatedResponse<Comment> items = commentService.getCommentList(CHALLENGE, challengeId, pageable, me);

        return ResponseEntity.ok(ResponseFactory.build(items));
    }

    @GetMapping(path = "tutorials/{tid}/comments")
    public ResponseEntity<?> getTutorialCommentList(@PathVariable("tid") Long tutorialId, @RequestParam(value = "size", defaultValue = "10") int size, @CurrentUser UserPrincipal me) {
        Pageable pageable = getPaginationConfig(1, size, "createdAt", "desc");

        PaginatedResponse<Comment> items = commentService.getCommentList(TUTORIAL, tutorialId, pageable, me);

        return ResponseEntity.ok(ResponseFactory.build(items));
    }
}

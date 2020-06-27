package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.request.UpdateCommentRequest;
import vn.candicode.payload.response.CommentDetails;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommentService;

import javax.validation.Valid;

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
        CommentDetails comment = commentService.addComment(CHALLENGE, challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PostMapping(path = "tutorials/{id}/comments")
    public ResponseEntity<?> addTutorialComment(@PathVariable("id") Long tutorialId, @RequestBody @Valid NewCommentRequest payload, @CurrentUser UserPrincipal me) {
        CommentDetails comment = commentService.addComment(TUTORIAL, tutorialId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PutMapping(path = "challenges/{clid}/comments/{cid}")
    public ResponseEntity<?> updateChallengeComment(@PathVariable("clid") Long challengeId, @PathVariable("cid") Long commentId, @RequestBody @Valid UpdateCommentRequest payload, @CurrentUser UserPrincipal me) {
        CommentDetails comment = commentService.updateComment(challengeId, CHALLENGE, commentId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PutMapping(path = "tutorials/{tid}/comments/{cid}")
    public ResponseEntity<?> updateTutorialComment(@PathVariable("tid") Long tutorialId, @PathVariable("cid") Long commentId, @RequestBody @Valid UpdateCommentRequest payload, @CurrentUser UserPrincipal me) {
        CommentDetails comment = commentService.updateComment(tutorialId, TUTORIAL, commentId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }
}

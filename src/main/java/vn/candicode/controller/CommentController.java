package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.common.CommentSubject;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.response.CommentDetails;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommentService;

import javax.validation.Valid;

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
        CommentDetails comment = commentService.addComment(CommentSubject.CHALLENGE, challengeId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }

    @PostMapping(path = "tutorials/{id}/comments")
    public ResponseEntity<?> addTutorialComment(@PathVariable("id") Long tutorialId, @RequestBody @Valid NewCommentRequest payload, @CurrentUser UserPrincipal me) {
        CommentDetails comment = commentService.addComment(TUTORIAL, tutorialId, payload, me);

        return ResponseEntity.ok(ResponseFactory.build(comment));
    }
}

package vn.candicode.service;

import org.springframework.data.domain.Pageable;
import vn.candicode.common.CommentSubject;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.request.UpdateCommentRequest;
import vn.candicode.payload.response.Comment;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.security.UserPrincipal;

import java.util.List;

public interface CommentService {
    /**
     * Create a new comment or reply an existing one
     *
     * @param subject   subject type, can be challenge or tutorial
     * @param subjectId id of challenge or tutorial
     * @param payload
     * @param author
     * @return details of new comment
     */
    Comment addComment(CommentSubject subject, Long subjectId, NewCommentRequest payload, UserPrincipal author);

    /**
     * @param subjectId   id of challenge or tutorial
     * @param subjectType challenge or tutorial
     * @param commentId
     * @param payload
     * @param currentUser only comment's owner can update it
     * @return details of updated comment
     */
    Comment updateComment(Long subjectId, CommentSubject subjectType, Long commentId, UpdateCommentRequest payload, UserPrincipal currentUser);

    /**
     * @param subjectId   id of challenge or tutorial
     * @param commentId
     * @param currentUser only comment's owner can delete it
     */
    void deleteComment(Long subjectId, Long commentId, UserPrincipal currentUser);

    /**
     * This service only get the comments that have no parent.
     * If you want to fetch their replies, call to {@link #getCommentReplies(Long, Pageable)}
     *
     * @param subject   subject type, can be challenge or tutorial
     * @param subjectId if of challenge or tutorial
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    PaginatedResponse<Comment> getCommentList(CommentSubject subject, Long subjectId, Pageable pageable, UserPrincipal me);


    /**
     * @param commentId parent comment's id
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    List<Comment> getCommentReplies(Long commentId, Pageable pageable);
}

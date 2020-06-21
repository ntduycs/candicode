package vn.candicode.service;

import org.springframework.data.domain.Pageable;
import vn.candicode.common.CommentSubject;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.response.CommentDetails;
import vn.candicode.payload.response.CommentSummary;
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
    CommentDetails addComment(CommentSubject subject, Long subjectId, NewCommentRequest payload, UserPrincipal author);

    /**
     * @param commentId
     * @param payload
     * @param currentUser only comment's owner can update it
     * @return details of updated comment
     */
    CommentDetails updateComment(Long commentId, NewCommentRequest payload, UserPrincipal currentUser);

    /**
     * @param commentId
     * @param currentUser only comment's owner can delete it
     */
    void deleteComment(Long commentId, UserPrincipal currentUser);

    /**
     * This service only get the comments that have no parent.
     * If you want to fetch their replies, call to {@link #getCommentReplies(Long, Pageable)}
     *
     * @param subject   subject type, can be challenge or tutorial
     * @param subjectId if of challenge or tutorial
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    PaginatedResponse<CommentSummary> getCommentList(CommentSubject subject, Long subjectId, Pageable pageable);


    /**
     * @param commentId parent comment's id
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    List<CommentSummary> getCommentReplies(Long commentId, Pageable pageable);
}

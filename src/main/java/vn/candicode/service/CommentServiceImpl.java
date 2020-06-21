package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.candicode.common.CommentSubject;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.response.CommentDetails;
import vn.candicode.payload.response.CommentSummary;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.repository.ChallengeCommentRepository;
import vn.candicode.repository.TutorialCommentRepository;
import vn.candicode.security.UserPrincipal;

import java.util.List;

@Service
@Log4j2
public class CommentServiceImpl implements CommentService {
    private final ChallengeCommentRepository challengeCommentRepository;
    private final TutorialCommentRepository tutorialCommentRepository;

    public CommentServiceImpl(ChallengeCommentRepository challengeCommentRepository, TutorialCommentRepository tutorialCommentRepository) {
        this.challengeCommentRepository = challengeCommentRepository;
        this.tutorialCommentRepository = tutorialCommentRepository;
    }

    /**
     * Create a new comment or reply an existing one
     *
     * @param subject   subject type, can be challenge or tutorial
     * @param subjectId id of challenge or tutorial
     * @param payload
     * @param author
     * @return details of new comment
     */
    @Override
    public CommentDetails addComment(CommentSubject subject, Long subjectId, NewCommentRequest payload, UserPrincipal author) {
        return null;
    }

    /**
     * @param commentId
     * @param payload
     * @param currentUser only comment's owner can update it
     * @return details of updated comment
     */
    @Override
    public CommentDetails updateComment(Long commentId, NewCommentRequest payload, UserPrincipal currentUser) {
        return null;
    }

    /**
     * @param commentId
     * @param currentUser only comment's owner can delete it
     */
    @Override
    public void deleteComment(Long commentId, UserPrincipal currentUser) {

    }

    /**
     * This service only get the comments that have no parent.
     * If you want to fetch their replies, call to {@link #getCommentReplies(Long, Pageable)}
     *
     * @param subject   subject type, can be challenge or tutorial
     * @param subjectId if of challenge or tutorial
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    @Override
    public PaginatedResponse<CommentSummary> getCommentList(CommentSubject subject, Long subjectId, Pageable pageable) {
        return null;
    }

    /**
     * @param commentId parent comment's id
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    @Override
    public List<CommentSummary> getCommentReplies(Long commentId, Pageable pageable) {
        return null;
    }
}

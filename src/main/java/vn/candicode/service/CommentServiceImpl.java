package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.CommentSubject;
import vn.candicode.entity.*;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewCommentRequest;
import vn.candicode.payload.request.UpdateCommentRequest;
import vn.candicode.payload.response.Comment;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.repository.ChallengeCommentRepository;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.TutorialCommentRepository;
import vn.candicode.repository.TutorialRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.CommentBeanUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.candicode.common.CommentSubject.CHALLENGE;

@Service
@Log4j2
public class CommentServiceImpl implements CommentService {
    private final ChallengeCommentRepository challengeCommentRepository;
    private final TutorialCommentRepository tutorialCommentRepository;
    private final ChallengeRepository challengeRepository;
    private final TutorialRepository tutorialRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CommentServiceImpl(ChallengeCommentRepository challengeCommentRepository, TutorialCommentRepository tutorialCommentRepository, ChallengeRepository challengeRepository, TutorialRepository tutorialRepository) {
        this.challengeCommentRepository = challengeCommentRepository;
        this.tutorialCommentRepository = tutorialCommentRepository;
        this.challengeRepository = challengeRepository;
        this.tutorialRepository = tutorialRepository;
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
    public Comment addComment(CommentSubject subject, Long subjectId, NewCommentRequest payload, UserPrincipal author) {
        if (subject.equals(CHALLENGE)) {
            return addChallengeComment(subjectId, payload, author);
        } else {
            return addTutorialComment(subjectId, payload, author);
        }
    }

    private Comment addChallengeComment(Long challengeId, NewCommentRequest payload, UserPrincipal me) {
        ChallengeEntity challenge = challengeRepository.findByChallengeIdFetchComments(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", challengeId));

        ChallengeCommentEntity comment = new ChallengeCommentEntity();

        comment.setAuthor(me.getFullName());
        comment.setContent(payload.getContent());
        comment.setLikes(0);
        comment.setDislikes(0);

        Map<Long /*comment_id*/, ChallengeCommentEntity> commentMap = challenge.getComments().stream()
            .collect(Collectors.toMap(CommentEntity::getCommentId, item -> item));

        if (payload.getParentId() != null && commentMap.containsKey(payload.getParentId())) {
            comment.setParent(commentMap.get(payload.getParentId()));
        }

        challenge.addComment(comment);

        challengeCommentRepository.save(comment);

        return CommentBeanUtils.details(comment, me);
    }

    private Comment addTutorialComment(Long tutorialId, NewCommentRequest payload, UserPrincipal me) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialIdFetchComments(tutorialId)
            .orElseThrow(() -> new ResourceNotFoundException(TutorialEntity.class, "id", tutorialId));

        TutorialCommentEntity comment = new TutorialCommentEntity();

        comment.setAuthor(me.getFullName());
        comment.setContent(payload.getContent());
        comment.setLikes(0);
        comment.setDislikes(0);

        Map<Long /*comment_id*/, TutorialCommentEntity> commentMap = tutorial.getComments().stream()
            .collect(Collectors.toMap(CommentEntity::getCommentId, item -> item));

        if (payload.getParentId() != null && commentMap.containsKey(payload.getParentId())) {
            comment.setParent(commentMap.get(payload.getParentId()));
        }

        tutorial.addComment(comment);

        tutorialCommentRepository.save(comment);

        return CommentBeanUtils.details(comment, me);
    }

    /**
     * @param subjectId   id of challenge or tutorial
     * @param commentId
     * @param subjectType challenge or tutorial
     * @param payload
     * @param currentUser only comment's owner can update it
     * @return details of updated comment
     */
    @Override
    @Transactional
    public Comment updateComment(Long subjectId, CommentSubject subjectType, Long commentId, UpdateCommentRequest payload, UserPrincipal currentUser) {
        CommentEntity comment;

        if (subjectType.equals(CHALLENGE)) {
            comment = updateChallengeComment(subjectId, commentId, payload.getContent(), currentUser);
        } else {
            comment = updateTutorialComment(subjectId, commentId, payload.getContent(), currentUser);
        }

        return CommentBeanUtils.details(comment, currentUser);
    }

    private CommentEntity updateChallengeComment(Long challengeId, Long commentId, String content, UserPrincipal me) {
        ChallengeCommentEntity comment = challengeCommentRepository.findByCommentIdAndChallengeId(commentId, challengeId)
            .orElseThrow(() -> new ResourceNotFoundException(CommentEntity.class, "commentId", commentId, "challengeId", challengeId));
        comment.setContent(content);

        return challengeCommentRepository.saveAndFlush(comment); // do flush to reflect the value of updatedAt intermediately
    }

    private CommentEntity updateTutorialComment(Long tutorialId, Long commentId, String content, UserPrincipal me) {
        TutorialCommentEntity comment = tutorialCommentRepository.findByCommentIdAndTutorialId(commentId, tutorialId)
            .orElseThrow(() -> new ResourceNotFoundException(CommentEntity.class, "commentId", commentId, "challengeId", tutorialId));
        comment.setContent(content);

        return tutorialCommentRepository.saveAndFlush(comment); // do flush to reflect the value of updatedAt intermediately
    }

    /**
     * @param subjectId   id of challenge or tutorial
     * @param commentId
     * @param currentUser only comment's owner can delete it
     */
    @Override
    @Transactional
    public void deleteComment(Long subjectId, Long commentId, UserPrincipal currentUser) {
        try {
            CommentEntity comment = entityManager.createQuery("select c from CommentEntity c where c.commentId = :id", CommentEntity.class)
                .setParameter("id", commentId)
                .getSingleResult();

            entityManager.remove(comment);
        } catch (NoResultException e) {
            throw new ResourceNotFoundException(CommentEntity.class, "id", commentId);
        }
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
    @Transactional(readOnly = true)
    public PaginatedResponse<Comment> getCommentList(CommentSubject subject, Long subjectId, Pageable pageable, UserPrincipal me) {
        if (subject.equals(CHALLENGE)) {
            Page<ChallengeCommentEntity> items = challengeCommentRepository.findAllByChallengeId(subjectId, pageable);
            List<Comment> details = items.map(item -> CommentBeanUtils.details(item, me)).getContent();
            return PaginatedResponse.<Comment>builder()
                .first(items.isFirst())
                .last(items.isLast())
                .page(items.getNumber())
                .size(items.getSize())
                .totalElements(items.getTotalElements())
                .totalPages(items.getTotalPages())
                .items(details)
                .build();
        } else {
            Page<TutorialCommentEntity> items = tutorialCommentRepository.findAllByTutorialId(subjectId, pageable);
            List<Comment> details = items.map(item -> CommentBeanUtils.details(item, me)).getContent();
            return PaginatedResponse.<Comment>builder()
                .first(items.isFirst())
                .last(items.isLast())
                .page(items.getNumber())
                .size(items.getSize())
                .totalElements(items.getTotalElements())
                .totalPages(items.getTotalPages())
                .items(details)
                .build();
        }
    }

    /**
     * @param commentId parent comment's id
     * @param pageable  only contain size and page parameters, default fetching with size = 10 and page = 0
     * @return
     */
    @Override
    public List<Comment> getCommentReplies(Long commentId, Pageable pageable) {
        return null;
    }
}

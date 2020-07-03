package vn.candicode.util;

import vn.candicode.entity.ChallengeCommentEntity;
import vn.candicode.entity.CommentEntity;
import vn.candicode.entity.TutorialCommentEntity;
import vn.candicode.payload.response.Comment;
import vn.candicode.security.UserPrincipal;

public class CommentBeanUtils {
    public static Comment details(CommentEntity entity, UserPrincipal me) {
        Comment details = new Comment();

        details.setAuthor(entity.getAuthor());
        details.setCommentId(entity.getCommentId());
        details.setContent(entity.getContent());
        details.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setDislikes(entity.getDislikes());
        details.setLikes(entity.getLikes());
        details.setParentId(entity.getParent() != null ? entity.getParent().getCommentId() : null);
        details.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setAvatar(null);
        details.setMe(me != null && me.getFullName().equals(entity.getAuthor()));
        details.setNumReplies(entity.getChildren() == null ? 0 : entity.getChildren().size());

        if (entity instanceof ChallengeCommentEntity) {
            details.setSubjectId(((ChallengeCommentEntity) entity).getChallenge().getChallengeId());
        } else {
            details.setSubjectId(((TutorialCommentEntity) entity).getTutorial().getTutorialId());
        }

        return details;
    }
}

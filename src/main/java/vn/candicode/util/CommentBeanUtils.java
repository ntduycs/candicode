package vn.candicode.util;

import vn.candicode.entity.CommentEntity;
import vn.candicode.payload.response.CommentDetails;

public class CommentBeanUtils {
    public static CommentDetails details(CommentEntity entity) {
        CommentDetails details = new CommentDetails();

        details.setAuthor(entity.getAuthor());
        details.setCommentId(entity.getCommentId());
        details.setContent(entity.getContent());
        details.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setDislikes(entity.getDislikes());
        details.setLikes(entity.getLikes());
        details.setParentId(entity.getParent() != null ? entity.getParent().getCommentId() : null);
        details.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));

        return details;
    }
}

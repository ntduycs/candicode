package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CommentDetails implements Serializable {
    private Long commentId;
    private String content;
    private Long parentId;
    private String createdAt;
    private String updatedAt;
    private Integer likes;
    private Integer dislikes;
    private String author;
    private Long subjectId; // challengeId or tutorialId
}

package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Comment implements Serializable {
    private Long commentId;
    private String content;
    private String author;
    /**
     * Avatar of author
     */
    private String avatar;
    private String createdAt;
    private String updatedAt;
    /**
     * Flag that indicates this is my comment
     */
    private Boolean me;
    private Integer numReplies;

    private Long parentId;
    private Integer likes;
    private Integer dislikes;

    private Long subjectId; // challengeId or tutorialId
}

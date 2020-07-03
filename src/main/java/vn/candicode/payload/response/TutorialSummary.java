package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TutorialSummary implements Serializable {
    private Long tutorialId;
    private String title;
    private String description;
    private String author;
    private Integer likes;
    private Integer dislikes;
    private String banner;
    private Integer numComments;
    private String createdAt;

    private Set<String> tags = new HashSet<>();
    private Set<String> categories = new HashSet<>();


}

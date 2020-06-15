package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TutorialSummary implements Serializable {
    private Long tutorialId;
    private String title;
    private String description;
    private List<String> tags = new ArrayList<>();
    private String banner;
    private Long numComments;
    private Integer likes;
    private Integer dislikes;
    private String author;
    private List<String> categories = new ArrayList<>();
    private String createdAt;
    private String updatedAt;
}

package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChallengeSummary implements Serializable {
    private Long challengeId;
    private String title;
    private String level;
    private Integer point;
    private String author;
    private String banner;
    private String createdAt;
    private String updatedAt;

    private List<String> languages = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private Long numComments;
    private Long numAttendees;
    private Integer likes;
    private Integer dislikes;

    private String slug;
}

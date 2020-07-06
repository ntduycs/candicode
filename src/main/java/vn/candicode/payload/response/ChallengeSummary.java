package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<String> tags = new HashSet<>();

    private Long numComments;
    private Long numAttendees;
    private Integer likes;
    private Integer dislikes;

    private String slug;

    private Boolean available;

    public Long getNumAttendees() {
        return numAttendees == null ? 0L : numAttendees;
    }
}

package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.response.sub.Challenge;
import vn.candicode.payload.response.sub.Testcase;
import vn.candicode.payload.response.sub.TestcaseFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ChallengeDetails implements Serializable {
    private Long challengeId;
    private String title;
    private String description;
    private String level;
    private Integer point;
    private String author;
    private String banner;
    private String createdAt;
    private String updatedAt;
    private Long numComments;
    private Long numAttendees;
    private Integer likes;
    private Integer dislikes;
    private TestcaseFormat tcInputFormat;
    private TestcaseFormat tcOutputFormat;

    private List<Challenge> contents = new ArrayList<>();
    private List<Testcase> testcases = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private Set<String> tags = new HashSet<>();

    private String slug;

    private Boolean available;

    public Long getNumAttendees() {
        return numAttendees != null ? numAttendees : 0L;
    }
}

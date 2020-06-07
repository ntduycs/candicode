package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private Float rate;
    private Integer numRates;

    private List<Challenge> contents = new ArrayList<>();
    private List<Testcase> testcases = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
}

package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubmissionHistory implements Serializable {
    private Long submissionId;

    private Long challengeId;
    private String challengeTitle;

    private Boolean compiled;

    private Integer point;
    private String author; // author's full name

    private Double execTime; // in nanoseconds
    private Double doneWithin; // in minutes

    private Integer passedTestcases;
    private Integer totalTestcases;

    private String createdAt;

    private Boolean contestChallenge;

    private String submitAt;
}

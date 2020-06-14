package vn.candicode.payloads.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestcaseResult implements Serializable {
    @JsonIgnore
    private Long testcaseId;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private String error;
    private Boolean passed;

    public TestcaseResult(Long testcaseId, boolean hidden, String input, String expectedOutput, String actualOutput, Boolean passed) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.expectedOutput = hidden ? "" : expectedOutput;
        this.actualOutput = actualOutput;
        this.passed = passed;
    }

    public TestcaseResult(Long testcaseId, boolean hidden, String input, String expectedOutput, String actualOutput, String error, Boolean passed) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.expectedOutput = hidden ? "" : expectedOutput;
        this.actualOutput = actualOutput;
        this.error = error;
        this.passed = passed;
    }
}

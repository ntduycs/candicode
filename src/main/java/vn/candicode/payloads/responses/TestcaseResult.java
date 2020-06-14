package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestcaseResult implements Serializable {
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private String error;
    private Boolean passed;

    public TestcaseResult(boolean hidden, String input, String expectedOutput, String actualOutput, Boolean passed) {
        this.input = input;
        this.expectedOutput = hidden ? "" : expectedOutput;
        this.actualOutput = actualOutput;
        this.passed = passed;
    }

    public TestcaseResult(boolean hidden, String input, String expectedOutput, String actualOutput, String error, Boolean passed) {
        this.input = input;
        this.expectedOutput = hidden ? "" : expectedOutput;
        this.actualOutput = actualOutput;
        this.error = error;
        this.passed = passed;
    }
}

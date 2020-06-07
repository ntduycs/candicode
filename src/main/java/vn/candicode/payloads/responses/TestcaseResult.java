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


    public TestcaseResult(boolean hidden, String input, String expectedOutput, String actualOutput) {
        this.input = input;
        this.expectedOutput = hidden ? "" : expectedOutput;
        this.actualOutput = actualOutput;
    }
}

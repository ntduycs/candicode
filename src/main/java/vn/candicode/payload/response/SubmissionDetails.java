package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SubmissionDetails implements Serializable {
    private Long testcaseId;
    private String input;
    private String expectedOutput;
    private String actualOutput; // null in case of runtime error
    private String error; // runtime error in detail
    private Boolean passed; // true if expectedOutput.equals(actualOutput) returns true
}

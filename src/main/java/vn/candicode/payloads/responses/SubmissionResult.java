package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubmissionResult implements Serializable {
    private Integer total;
    private Integer passed;
    private String compiled;
    private String error;
    private List<TestcaseResult> details = new ArrayList<>();
}

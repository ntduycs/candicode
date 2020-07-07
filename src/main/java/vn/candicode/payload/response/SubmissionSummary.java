package vn.candicode.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class SubmissionSummary implements Serializable {
    private Integer total; // number of testcases
    private Integer passed; // number of passed testcases
    private String compiled; // compile status, 'success' or 'failed'
    private String error; // compile error in detail
    private List<SubmissionDetails> details; // detailed results for each testcases, empty if failed to compile
    private String submitAt;
}

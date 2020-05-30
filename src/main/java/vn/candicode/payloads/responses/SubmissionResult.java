package vn.candicode.payloads.responses;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class SubmissionResult implements Serializable {
    private Double executionTime;
    private Double usedMemory;

    @NonNull
    private Integer passedTestcases;

    @NonNull
    private Integer totalTestcases;

    @NonNull
    private String message;

    @NonNull
    private List<TestcaseResult> details;
}

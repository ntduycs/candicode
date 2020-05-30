package vn.candicode.payloads.responses;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class TestcaseResult implements Serializable {
    private String expectedOutput;

    private String actualOutput;

    private String error;

    @NonNull
    private Boolean passed;
}

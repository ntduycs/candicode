package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Testcase implements Serializable {
    private Long testcaseId;
    private String input;
    private String output;
    private Boolean hidden;

    public Testcase(Long testcaseId, String input, String output, Boolean hidden) {
        this.input = input;
        this.hidden = hidden;
        this.output = hidden ? "" : output;
    }
}

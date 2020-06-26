package vn.candicode.payload.response.sub;

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
        this.testcaseId = testcaseId;
        this.input = input;
        this.hidden = hidden;
        this.output = hidden ? null : output;
    }

    public Testcase(Long testcaseId, String input, String output, Boolean hidden, Boolean isOwner) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.hidden = hidden;
        if (isOwner) {
            this.output = output;
        } else if (hidden) {
            this.output = null;
        } else {
            this.output = output;
        }
    }
}

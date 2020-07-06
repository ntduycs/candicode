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

    public Testcase(Long testcaseId, String input, String output, Boolean hidden, Boolean displayable) {
        this.testcaseId = testcaseId;
        this.input = input;
        this.hidden = hidden;
        if (displayable || !hidden) {
            this.output = output;
        } else {
            this.output = null;
        }
    }
}

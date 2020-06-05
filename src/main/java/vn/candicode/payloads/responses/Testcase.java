package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Testcase implements Serializable {
    private String input;
    private String output;
    private Boolean hidden;

    public Testcase(String input, String output, Boolean hidden) {
        this.input = input;
        this.hidden = hidden;
        this.output = hidden ? "" : output;
    }
}

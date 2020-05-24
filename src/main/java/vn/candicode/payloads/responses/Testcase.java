package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Getter
@Setter
public class Testcase implements Serializable {

    private String input;

    private String output;

    private Boolean publicTestcase;

    public Testcase(String input, @Nullable String output, Boolean publicTestcase) {
        this.input = input;
        this.output = output;
        this.publicTestcase = publicTestcase;
    }

}

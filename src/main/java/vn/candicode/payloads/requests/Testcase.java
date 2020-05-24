package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Testcase {
    @NotBlank(message = "Field 'input' is required but not be given")
    private String input;

    @NotBlank(message = "Field 'output' is required but not be given")
    private String output;

    @NotNull(message = "Field 'publicTestcase' is required but not be given")
    private Boolean publicTestcase;

    /**
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("Input: %s, Output: %s", input, output);
    }
}

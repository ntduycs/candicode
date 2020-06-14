package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdatedTestcase {
    @NotNull(message = "Field 'testcaseId' is required but not be given")
    private Long testcaseId;

    @NotBlank(message = "Field 'input' is required but not be given")
    private String input;

    @NotBlank(message = "Field 'output' is required but not be given")
    private String output;

    private Boolean hidden;
}

package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TestcaseVerificationRequest extends GenericRequest {
    @NotBlank(message = "Field 'input' is required but not be given")
    private String input;
}

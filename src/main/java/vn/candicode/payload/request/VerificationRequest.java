package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VerificationRequest extends Request {
    @NotBlank(message = "Field 'input' is required but not be given")
    private String input;

    private long timeout; // in millis
}

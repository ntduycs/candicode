package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordRequest extends Request {
    @NotBlank(message = "Field 'oldPassword' is required but not be given")
    private String oldPassword;

    @NotBlank(message = "Field 'newPassword' is required but not be given")
    private String newPassword;

    @NotBlank(message = "Field 'confirmPassword' is required but not be given")
    private String confirmPassword;
}

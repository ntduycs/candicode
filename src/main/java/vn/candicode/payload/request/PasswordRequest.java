package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.request.validator.PasswordConfirm;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@PasswordConfirm
public class PasswordRequest extends Request implements PasswordConfirmable {
    @NotBlank(message = "Field 'oldPassword' is required but not be given")
    private String oldPassword;

    @NotBlank(message = "Field 'newPassword' is required but not be given")
    private String newPassword;

    @NotBlank(message = "Field 'confirmPassword' is required but not be given")
    private String confirmPassword;

    @Override
    public String getPassword() {
        return newPassword;
    }
}

package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.validators.Email;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest extends BaseRequest {
    @NotBlank(message = "Field 'email' is required but not be given")
    @Email
    private String email;

    @NotBlank(message = "Field 'password' is required but not be given")
    private String password;
}

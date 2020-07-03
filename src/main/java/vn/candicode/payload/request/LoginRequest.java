package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest extends Request {
    @NotBlank(message = "Field 'email' is required but not be given")
    @Email
    private String email;

    @NotBlank(message = "Field 'password' is required but not be given")
    private String password;
}

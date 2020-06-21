package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class NewStudentRequest extends Request {
    @NotBlank(message = "Field 'email' is required but not be given")
    @Email
    private String email;

    @NotBlank(message = "Field 'password' is required but not be given")
    @Size(min = 6, message = "Password should contain at least 6 characters")
    private String password;

    @NotBlank(message = "Field 'confirmPassword' is required but not be given")
    private String confirmPassword;

    @NotBlank(message = "Field 'firstName' is required but not be given")
    private String firstName;

    @NotBlank(message = "Field 'lastName' is required but not be given")
    private String lastName;
}

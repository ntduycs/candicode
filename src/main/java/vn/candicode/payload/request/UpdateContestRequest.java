package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Getter
@Setter
public class UpdateContestRequest extends Request {
    @NotBlank(message = "Field 'title' is required but not be given")
    private String title;

    @NotNull(message = "Field 'registrationDeadline' is required but not be given")
    @Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9]).(\\d{3})$")
    // yyyy-MM-dd HH:mm:ss.SSS
    private String registrationDeadline;

    @NotBlank(message = "Field 'description' is required but not be given")
    private String description;

    @NotBlank(message = "Field 'content' is required but not be given")
    private String content;

    private Set<String> tags;

    private MultipartFile banner;

    private Integer maxRegister;
}

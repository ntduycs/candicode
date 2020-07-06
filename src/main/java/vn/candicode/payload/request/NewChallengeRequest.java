package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.payload.request.validator.Image;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class NewChallengeRequest extends Request {
    @NotBlank(message = "Field 'title' is required but not be given")
    private String title;

    @NotBlank(message = "Field 'level' is required but not be given")
    private String level;

    @NotBlank(message = "Field 'description is required but not be given")
    private String description;

    @Image
    private MultipartFile banner;

    @NotBlank(message = "Field 'language' is required but not be given")
    private String language;

    @NotBlank(message = "Field 'runPath' is required but not be given")
    private String runPath;

    private String compilePath;

    @NotBlank(message = "Field 'implementedPath' is required but not be given")
    private String implementedPath;

    @NotBlank(message = "Field 'nonImplementedPath' is required but not be given")
    private String nonImplementedPath;

    @NotNull(message = "Field 'tcInputFormat' is required but not be given")
    @Size(min = 1, message = "Field 'tcInputFormat' must contain at least 1 element")
    private List<String> tcInputFormat;

    @NotNull(message = "Field 'tcOutputFormat' is required but not be given")
    @Size(min = 1, message = "Field 'tcOutputFormat' must contain at least 1 element")
    private List<String> tcOutputFormat;

    @NotBlank(message = "Field 'challengeDir' is required but not be given")
    private String challengeDir;

    private Set<String> tags;

    private Set<String> categories;

    private Boolean contestChallenge;

    public Boolean getContestChallenge() {
        return contestChallenge == null ? false : contestChallenge;
    }
}

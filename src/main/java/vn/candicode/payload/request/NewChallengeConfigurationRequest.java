package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewChallengeConfigurationRequest extends Request {
    @NotBlank(message = "Field 'language' is required but not be given")
    private String language;

    @NotBlank(message = "Field 'runPath' is required but not be given")
    private String runPath;

    @NotBlank(message = "Field 'compilePath' is required but not be given")
    private String compilePath;

    @NotBlank(message = "Field 'implementedPath' is required but not be given")
    private String implementedPath;

    @NotBlank(message = "Field 'nonImplementedPath' is required but not be given")
    private String nonImplementedPath;

    @NotBlank(message = "Field 'challengeDir' is required but not be given")
    private String challengeDir;
}

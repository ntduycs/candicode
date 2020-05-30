package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.validators.Enum;
import vn.candicode.payloads.validators.File;
import vn.candicode.payloads.validators.Regex;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ChallengeRequest extends ChallengeMultipartRequest {
    @NotBlank(message = "Field 'title' is required but not be given")
    private String title;

    @NotBlank(message = "Field 'level' is required but not be given")
    @Enum(target = ChallengeLevel.class)
    private String level;

    @NotBlank(message = "Field 'description is required but not be given")
    private String description;

    @File(mimes = {"image/jpeg", "image/png"}, required = false)
    private MultipartFile banner;

    @NotBlank(message = "Field 'language' is required but not be given")
    @Enum(target = ChallengeLanguage.class)
    private String language;

    @NotBlank(message = "Field 'runPath' is required but not be given")
    private String runPath;

    @NotBlank(message = "Field 'compilePath' is required but not be given")
    private String compilePath;

    @NotBlank(message = "Field 'implementedPath' is required but not be given")
    private String implementedPath;

    @NotBlank(message = "Field 'nonImplementedPath' is required but not be given")
    private String nonImplementedPath;

    @NotBlank(message = "Field 'tcInputFormat' is required but not be given")
    @Regex
    private String tcInputFormat;

    @NotBlank(message = "Field 'tcOutputFormat' is required but not be given")
    @Regex
    private String tcOutputFormat;

    @NotBlank(message = "Field 'challengeDir' is required but not be given")
    private String challengeDir;
}

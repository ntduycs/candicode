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
public class ChallengeRequest extends MultipartRequest {
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

    @NotBlank(message = "Field 'targetPath' is required but not be given")
    private String targetPath;

    @NotBlank(message = "Field 'buildPath' is required but not be given")
    private String buildPath;

    @NotBlank(message = "Field 'editPath' is required but not be given")
    private String editPath;

    @NotBlank(message = "Field 'tcInputFormat' is required but not be given")
    @Regex
    private String tcInputFormat;

    @NotBlank(message = "Field 'tcOutputFormat' is required but not be given")
    @Regex
    private String tcOutputFormat;
}

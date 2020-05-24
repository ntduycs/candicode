package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.payloads.validators.Enum;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ChallengeConfigRequest {

    @NotBlank(message = "Field 'language' is required but not be given")
    @Enum(target = ChallengeLanguage.class)
    private String language;

    @NotBlank(message = "Field 'targetPath' is required but no be given")
    private String targetPath;

    @NotBlank(message = "Field 'buildPath' is required but not be given")
    private String buildPath;

    @NotBlank(message = "Field 'editPath' is required but not be given")
    private String editPath;

    @Enum(target = ChallengeLanguage.class)
    private List<String> removedLanguages;

}

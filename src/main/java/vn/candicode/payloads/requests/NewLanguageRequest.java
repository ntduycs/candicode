package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.payloads.GenericRequest;
import vn.candicode.payloads.validators.Belong2Enum;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewLanguageRequest extends GenericRequest {
    @NotBlank(message = "Field 'language' is required but not be given")
    @Belong2Enum(target = LanguageName.class)
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

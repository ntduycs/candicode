package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.payloads.validators.Enum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SubmissionRequest extends BaseRequest {
    @NotNull(message = "Field 'challengeId' is required but not be given")
    private Long challengeId;

    @NotBlank(message = "Field 'code' is required but not be given")
    private String code;

    @NotBlank(message = "Field 'codeLanguage' is required but not be given")
    @Enum(target = ChallengeLanguage.class)
    private String codeLanguage;
}

package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.payloads.validators.Enum;

import java.util.List;

@Getter
@Setter
public class ChallengeConfigRequest {

    @Enum(target = ChallengeLanguage.class)
    private String language;

    private String targetPath;

    private String buildPath;

    private String editPath;

    @Enum(target = ChallengeLanguage.class)
    private List<String> removedLanguages;

    private String crudaction;

}

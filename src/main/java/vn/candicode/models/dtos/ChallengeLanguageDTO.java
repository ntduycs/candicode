package vn.candicode.models.dtos;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.enums.LanguageName;

import java.io.Serializable;

@Getter
@Setter
public class ChallengeLanguageDTO implements Serializable {
    private Long challengeConfigId;
    private LanguageName text;

    public ChallengeLanguageDTO(Long challengeConfigId, LanguageName text) {
        this.challengeConfigId = challengeConfigId;
        this.text = text;
    }
}

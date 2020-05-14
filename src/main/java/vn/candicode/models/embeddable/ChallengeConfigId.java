package vn.candicode.models.embeddable;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
public class ChallengeConfigId implements Serializable {
    @NonNull
    private Long challengeId;

    @NonNull
    private Long languageId;
}

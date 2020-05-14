package vn.candicode.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "challenge_langs")
public class ChallengeLanguage extends BaseModel {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private vn.candicode.models.enums.ChallengeLanguage name;

    @Column(nullable = false)
    private Long totalChallenges = 0L;
}

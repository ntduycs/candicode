package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "contest_challenges", uniqueConstraints = {
    @UniqueConstraint(name = "contest_round_challenge_idx", columnNames = {"contest_round_id", "challenge_id"})
})
public class ContestChallengeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long contestChallengeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_round_id", nullable = false, foreignKey = @ForeignKey(name = "contest_round_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ContestRoundEntity contestRound;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_fk"))
    private ChallengeEntity challenge;

    public ContestChallengeEntity() {
    }

    public ContestChallengeEntity(ContestRoundEntity contestRound, ChallengeEntity challenge) {
        this.contestRound = contestRound;
        this.challenge = challenge;
    }
}

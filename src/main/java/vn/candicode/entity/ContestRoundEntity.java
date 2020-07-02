package vn.candicode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "contest_rounds")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = false, of = "contestRoundId")
public class ContestRoundEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long contestRoundId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private Long duration; // in minutes

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false, foreignKey = @ForeignKey(name = "contest_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ContestEntity contest;

    @OneToMany(mappedBy = "contestRound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestChallengeEntity> challenges = new ArrayList<>();

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public void addChallenge(ChallengeEntity challenge) {
        ContestChallengeEntity contestChallenge = new ContestChallengeEntity(this, challenge);
        challenges.add(contestChallenge);
    }

    public void removeChallenge(ChallengeEntity challenge) {
        for (Iterator<ContestChallengeEntity> iterator = challenges.iterator(); iterator.hasNext(); ) {
            ContestChallengeEntity contestChallenge = iterator.next();
            if (contestChallenge.getContestRound().equals(this) && contestChallenge.getChallenge().equals(challenge)) {
                iterator.remove();
                contestChallenge.setChallenge(null);
                contestChallenge.setContestRound(null);
                break;
            }
        }
    }

    public void removeChallenge(Long challengeId) {
        for (Iterator<ContestChallengeEntity> iterator = challenges.iterator(); iterator.hasNext(); ) {
            ContestChallengeEntity contestChallenge = iterator.next();
            if (contestChallenge.getContestRound().getContestRoundId().equals(this.getContestRoundId()) && contestChallenge.getChallenge().getChallengeId().equals(challengeId)) {
                iterator.remove();
                contestChallenge.setChallenge(null);
                contestChallenge.setContestRound(null);
                break;
            }
        }
    }
}

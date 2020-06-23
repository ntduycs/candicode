package vn.candicode.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "contest_rounds")
public class ContestRoundEntity extends GenericEntity {
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

    public void addChallenge(ChallengeEntity challenge) {
        ContestChallengeEntity contestChallenge = new ContestChallengeEntity(this, challenge);
        challenges.add(contestChallenge);
    }

    public void removeCategory(ChallengeEntity challenge) {
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
}

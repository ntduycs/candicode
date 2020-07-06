package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "challenge_configurations", uniqueConstraints = {
    @UniqueConstraint(name = "challenge_language_idx", columnNames = {"challenge_id", "language_id"})
})
@Where(clause = "deleted = false")
public class ChallengeConfigurationEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeLanguageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false, foreignKey = @ForeignKey(name = "language_fk"))
    private LanguageEntity language;

    @Column(columnDefinition = "boolean default false")
    private Boolean enabled = false;

    @Column(nullable = false)
    private String directory;

    @Column(nullable = false)
    private String root;

    @Column(nullable = false)
    private String runScript;

    @Column
    private String compileScript;

    @Column(nullable = false)
    private String preImplementedFile;

    @Column(nullable = false)
    private String nonImplementedFile;

    /**
     * This is an redundant field used to eliminate JOIN query in case of submission.
     * This should be presented because submission is likely the most popular task in our system
     */
    @Column(nullable = false)
    private Long authorId;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public ChallengeConfigurationEntity() {
    }

    public ChallengeConfigurationEntity(ChallengeEntity challenge, LanguageEntity language) {
        this.challenge = challenge;
        this.language = language;
    }
}

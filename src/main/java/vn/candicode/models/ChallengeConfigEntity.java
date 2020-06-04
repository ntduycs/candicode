package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"challengeConfigId"}, callSuper = false)
@Entity
@Table(name = "challenge_configs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"challenge_id", "language_id"})
})
public class ChallengeConfigEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeConfigId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", referencedColumnName = "challengeId")
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", referencedColumnName = "languageId")
    private LanguageEntity language;

    @Column
    protected Boolean compatible = false;

    @Column(nullable = false)
    private String challengeDir;

    @Column(nullable = false)
    private String runPath;

    @Column
    private String compilePath;

    @Column(nullable = false)
    private String implementedPath;

    @Column(nullable = false)
    private String nonImplementedPath;
}

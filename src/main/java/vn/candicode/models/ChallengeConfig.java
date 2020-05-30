package vn.candicode.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.candicode.models.embeddable.ChallengeConfigId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "challenge_configs")
@EntityListeners({AuditingEntityListener.class})
public class ChallengeConfig implements Serializable {
    @EmbeddedId
    private ChallengeConfigId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("challengeId")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("languageId")
    private ChallengeLanguage language;

    @Column
    private Boolean compatible = false;

    @Column(nullable = false)
    private String challengeDir;

    @Column(nullable = false)
    private String implementedPath;

    @Column()
    private String compilePath;

    @Column(nullable = false)
    private String runPath;

    @Column(nullable = false)
    private String nonImplementedPath;

    @CreatedDate
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS")
    private LocalDateTime updatedAt;

    @JsonIgnore
    private LocalDateTime deletedAt;

    public ChallengeConfig(Challenge challenge, ChallengeLanguage language, String runPath, String compilePath, String implementedPath, String nonImplementedPath, String challengeDir) {
        this.challenge = challenge;
        this.language = language;
        this.id = new ChallengeConfigId(challenge.getId(), language.getId());
        this.runPath = runPath;
        this.compilePath = compilePath;
        this.implementedPath = implementedPath;
        this.nonImplementedPath = nonImplementedPath;
        this.challengeDir = challengeDir;
    }
}

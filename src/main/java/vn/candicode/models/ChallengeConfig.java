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
@ToString
@EqualsAndHashCode
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

    @Column(nullable = false)
    private String targetPath;

    @Column(nullable = false)
    private String buildPath;

    @Column(nullable = false)
    private String editPath;

    @CreatedDate
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss.SS")
    private LocalDateTime updatedAt;

    @JsonIgnore
    private LocalDateTime deletedAt;

    public ChallengeConfig(Challenge challenge, ChallengeLanguage language, String targetPath, String buildPath, String editPath) {
        this.challenge = challenge;
        this.language = language;
        this.id = new ChallengeConfigId(challenge.getId(), language.getId());
        this.targetPath = targetPath;
        this.buildPath = buildPath;
        this.editPath = editPath;
    }
}

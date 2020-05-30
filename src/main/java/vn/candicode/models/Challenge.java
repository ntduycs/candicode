package vn.candicode.models;

import lombok.*;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.models.enums.ChallengeLevel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true, exclude = {"description"})
@Entity
@Table(name = "challenges", indexes = {@Index(columnList = "title")})
public class Challenge extends PartialBaseModel {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    @NonNull
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(nullable = false)
    @NonNull
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ChallengeLevel level;

    @Column(nullable = false)
    @Lob
    @NonNull
    private String description;

    @Column(nullable = false)
    @NonNull
    private Integer points;

    private String bannerPath;

    @Column(nullable = false, name = "tc_input_format")
    @NonNull
    private String testcaseInputFormat;

    @Column(nullable = false, name = "tc_output_format")
    @NonNull
    private String testcaseOutputFormat;

    @OneToMany(
        mappedBy = "challenge",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ChallengeConfig> configurations = new ArrayList<>();

    public void addConfig(ChallengeConfig config) {
        configurations.add(config);
    }

    public void addConfigs(List<ChallengeConfig> configs) {
        configurations.addAll(configs);
    }

    public void removeConfig(ChallengeLanguage language) {
        for (Iterator<ChallengeConfig> iterator = configurations.iterator(); iterator.hasNext(); ) {
            ChallengeConfig config = iterator.next();

            if (config.getChallenge().equals(this) && config.getLanguage().getName().equals(language)) {
                iterator.remove();
                config.setDeletedAt(LocalDateTime.now());
            }
        }
    }

    public void removeConfigs(List<ChallengeConfig> configs) {
        if (configurations != null) {
            for (Iterator<ChallengeConfig> iterator = configurations.iterator(); iterator.hasNext(); ) {
                ChallengeConfig config = iterator.next();

                if (configs.contains(config)) {
                    iterator.remove();
                    config.setDeletedAt(LocalDateTime.now());
                }
            }
        }
    }

    @OneToMany(
        mappedBy = "challenge",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ChallengeTestcase> testcases = new ArrayList<>();

    public void addTestcase(ChallengeTestcase testcase) {
        testcases.add(testcase);
        testcase.setChallenge(this);
    }

    public void removeTestcase(ChallengeTestcase testcase) {
        testcases.remove(testcase);
        testcase.setChallenge(null);
    }

    public void addTestcases(List<ChallengeTestcase> testcases) {
        this.testcases.addAll(testcases);
        testcases.forEach(t -> t.setChallenge(this));
    }
}

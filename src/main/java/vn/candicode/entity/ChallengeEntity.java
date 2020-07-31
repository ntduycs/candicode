package vn.candicode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import vn.candicode.converter.StringToListConverter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "challenges", uniqueConstraints = {@UniqueConstraint(name = "challenge_title_idx", columnNames = {"title"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
@EqualsAndHashCode(of = {"title"}, callSuper = false)
public class ChallengeEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeId;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "text")
    private String description;

    @Column
    private String banner;

    @Column
    private Long maxPoint;

    @Column(nullable = false)
    private String inputFormat;

    @Column(nullable = false)
    private String outputFormat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private UserEntity author;

    @Column
    private String authorName;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestcaseEntity> testcases = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChallengeCategoryEntity> categories = new HashSet<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeConfigurationEntity> configurations = new ArrayList<>();

    @Convert(converter = StringToListConverter.class)
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false)
    private Boolean contestChallenge = false;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer dislikes = 0;

    @Column(columnDefinition = "boolean default false")
    private Boolean available = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @Convert(converter = StringToListConverter.class)
    private Set<String> languages = new LinkedHashSet<>();

    public boolean isContestChallenge() {
        return contestChallenge;
    }

    public void addTestcases(List<TestcaseEntity> testcases) {
        for (TestcaseEntity testcase : testcases) {
            addTestcase(testcase);
        }
    }

    public void addTestcase(TestcaseEntity testcase) {
        testcases.add(testcase);
        testcase.setChallenge(this);
    }

    public void removeTestcases(List<TestcaseEntity> testcases) {
        for (TestcaseEntity testcase : testcases) {
            removeTestcase(testcase);
        }
    }

    public void removeTestcase(TestcaseEntity testcase) {
        testcases.remove(testcase);
        testcase.setChallenge(null);
    }

    public void addComment(ChallengeCommentEntity comment) {
        comments.add(comment);
        comment.setChallenge(this);
    }

    public void removeComment(ChallengeCommentEntity comment) {
        comments.remove(comment);
        comment.setChallenge(null);
    }

    public void addCategory(CategoryEntity category) {
        ChallengeCategoryEntity challengeCategory = new ChallengeCategoryEntity(this, category);
        categories.add(challengeCategory);
    }

    public void removeCategory(CategoryEntity category) {
        for (Iterator<ChallengeCategoryEntity> iterator = categories.iterator(); iterator.hasNext(); ) {
            ChallengeCategoryEntity challengeCategory = iterator.next();
            if (challengeCategory.getChallenge().equals(this) && challengeCategory.getCategory().equals(category)) {
                iterator.remove();
                challengeCategory.setChallenge(null);
                challengeCategory.setCategory(null);
                break;
            }
        }
    }

    public void addConfiguration(ChallengeConfigurationEntity configuration) {
        if (configuration.getChallenge() == null) {
            this.configurations.add(configuration);
            configuration.setChallenge(this);
        }
    }

    public void setMaxPoint(String level) {
        switch (level) {
            case "easy":
                this.maxPoint = 100L;
                break;
            case "moderate":
                this.maxPoint = 200L;
                break;
            case "hard":
                this.maxPoint = 300L;
                break;
            default:
                this.maxPoint = -1L;
        }
    }
}

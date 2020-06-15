package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import vn.candicode.models.converters.TagConverter;
import vn.candicode.models.enums.ChallengeLevel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"challengeId"}, callSuper = false)
@Entity
@Table(name = "challenges")
public class ChallengeEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeLevel level;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    @Type(type = "text")
    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Column
    private String banner;

    @Column
    private Integer point;

    @Column(nullable = false, name = "tc_in_format")
    private String testcaseInputFormat;

    @Column(nullable = false, name = "tc_out_format")
    private String testcaseOutputFormat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestcaseEntity> testcases = new ArrayList<>();

    public void addTestcase(TestcaseEntity testcase) {
        testcases.add(testcase);
        testcase.setChallenge(this);
    }

    public void removeTestcase(TestcaseEntity testcase) {
        testcases.remove(testcase);
        testcase.setChallenge(null);
    }

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeCommentEntity> comments = new ArrayList<>();

    public void addComment(ChallengeCommentEntity comment) {
        comments.add(comment);
        comment.setChallenge(this);
    }

    public void removeComment(ChallengeCommentEntity comment) {
        comments.remove(comment);
        comment.setChallenge(null);
    }

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeCategoryEntity> categories = new ArrayList<>();

    public void addCategory(CategoryEntity category) {
        this.categories.add(new ChallengeCategoryEntity(this, category));
    }

    public void removeCategory(CategoryEntity category) {
        for (Iterator<ChallengeCategoryEntity> iterator = categories.iterator(); iterator.hasNext(); ) {
            ChallengeCategoryEntity entity = iterator.next();
            if (entity.getChallenge().equals(this) && entity.getCategory().equals(category)) {
                iterator.remove();
                entity.setChallenge(null);
                entity.setCategory(null);
            }
        }
    }

    @Convert(converter = TagConverter.class)
    private List<String> tags = new ArrayList<>();

    private boolean contestChallenge = false;
}

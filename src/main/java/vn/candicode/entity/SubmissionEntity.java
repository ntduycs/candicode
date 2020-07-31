package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "submissions")
public class SubmissionEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long submissionId;

    @Column(nullable = false)
    private Boolean compiled = false;

    @Column
    private Double doneWithin; // in minutes

    @Column
    private Double execTime; // in nanoseconds

    @Column(nullable = false)
    private Long point;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "text")
    private String submittedCode;

    // The following 2 fields helps us to eliminate JOIN clause when constructing submission history
    @Column(columnDefinition = "integer default 0")
    private Integer passedTestcases = 0;

    @Column(columnDefinition = "integer default 0")
    private Integer totalTestcases = 0;

    @Column
    private String authorName;

    @Column
    private String submitAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private StudentEntity author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private LanguageEntity language;
}

package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"submissionId"})
@Entity
@Table(name = "submissions")
public class SubmissionEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long submissionId;

    @Column(nullable = false)
    private boolean compileSuccess;

    @Column
    private Double completionTime;

    @Column
    private Double executionTime;

    @Column
    private Double usedMemory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private StudentEntity author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultEntity> results = new ArrayList<>();

    public void addResult(ResultEntity result) {
        results.add(result);
        result.setSubmission(this);
    }
}

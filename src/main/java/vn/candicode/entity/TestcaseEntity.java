package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "testcases")
public class TestcaseEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long testcaseId;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    private String expectedOutput;

    @Column(nullable = false)
    private Boolean hidden = false;

    @Column(columnDefinition = "bigint default 0")
    private Long timeout; // in nanoseconds

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;

    @Transient
    public boolean isPublicTestcase() {
        return !hidden;
    }

    public TestcaseEntity() {
    }

    public TestcaseEntity(String input, String expectedOutput, Boolean hidden) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
        this.timeout = 0L;
    }

    public TestcaseEntity(String input, String expectedOutput, Boolean hidden, Long timeout) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
        this.timeout = timeout;
    }
}

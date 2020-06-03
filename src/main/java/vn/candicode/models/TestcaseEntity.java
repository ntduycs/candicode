package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"testcaseId"}, callSuper = false)
@Entity
@Table(name = "testcases", uniqueConstraints = {@UniqueConstraint(columnNames = {"input", "output"})})
public class TestcaseEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long testcaseId;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false, name = "output")
    private String expectedOutput;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;
}

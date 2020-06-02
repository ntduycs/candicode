package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"testcaseId"}, callSuper = false)
@Entity
@Table(name = "testcases", uniqueConstraints = {@UniqueConstraint(columnNames = {"input", "output"})})
@SQLDelete(sql = "update testcase set deleted_at = now() where id = ?")
@Loader(namedQuery = "findTestcaseEntityByTestcaseId")
@NamedQuery(name = "findTestcaseEntityByTestcaseId", query = "select u from TestcaseEntity u where u.id = ?1 and u.deletedAt is null")
@Where(clause = "deleted_at is null")
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

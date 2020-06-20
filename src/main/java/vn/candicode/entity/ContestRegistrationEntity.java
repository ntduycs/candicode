package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "contest_registrations", uniqueConstraints = {
    @UniqueConstraint(name = "contest_student_idx", columnNames = {"contest_id", "student_id"})
})
public class ContestRegistrationEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long contestRegistrationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "student_fk"))
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false, foreignKey = @ForeignKey(name = "contest_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ContestEntity contest;

    public ContestRegistrationEntity() {
    }

    public ContestRegistrationEntity(StudentEntity student, ContestEntity contest) {
        this.student = student;
        this.contest = contest;
    }
}
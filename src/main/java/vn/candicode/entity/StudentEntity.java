package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("student")
public class StudentEntity extends UserEntity {
    @Column
    private Long gainedPoint = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_plan_id", foreignKey = @ForeignKey(name = "student_plan_fk"))
    private StudentPlanEntity studentPlan;
}

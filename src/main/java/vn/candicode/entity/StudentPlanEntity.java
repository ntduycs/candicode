package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "student_plans", uniqueConstraints = {@UniqueConstraint(name = "plan_name_idx", columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NaturalIdCache
public class StudentPlanEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long studentPlanId;

    @NaturalId
    @Column(nullable = false)
    private String name;

    @Column
    private Long duration; // in days
}

package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import vn.candicode.models.enums.PlanName;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@Entity
@Table(name = "plans")
public class PlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NaturalId
    private PlanName name;

    @Column
    private Long validityPeriod;
}

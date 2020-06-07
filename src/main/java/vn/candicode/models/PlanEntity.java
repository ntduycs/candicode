package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import vn.candicode.models.enums.PlanName;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@Entity
@Table(name = "plans")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class PlanEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NaturalId
    private PlanName text;

    @Column
    private Long validityPeriod;
}

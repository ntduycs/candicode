package vn.candicode.models;

import lombok.*;
import vn.candicode.models.embeddable.CoderPlan;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coders")
public class Coder extends User {
    @Embedded
    private CoderPlan plan;

    @Column(columnDefinition = "bigint default 0")
    private Long accruedPoint = 0L;

    public Coder(String email, String password, String firstName, String lastName, vn.candicode.models.enums.CoderPlan plan) {
        super(email, password, firstName, lastName);
        this.plan = new CoderPlan(plan);
    }
}

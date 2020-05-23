package vn.candicode.models.embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
public class CoderPlan implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_name", nullable = false)
    private vn.candicode.models.enums.CoderPlan name;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    @Column(name = "plan_issued_at")
    private LocalDate planIssuedAt;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    @Column(name = "plan_expired_at")
    private LocalDate planExpiredAt;

    public CoderPlan(vn.candicode.models.enums.CoderPlan plan) {
        this.name = plan;

        LocalDate now = LocalDate.now();

        this.planIssuedAt = now;
        this.planExpiredAt = plan.equals(vn.candicode.models.enums.CoderPlan.BASIC) ? null : LocalDate.from(now).plusMonths(1);
    }
}

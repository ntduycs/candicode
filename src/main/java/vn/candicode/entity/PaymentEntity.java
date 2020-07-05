package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentEntity extends Auditable {
    @Id
    @Column(nullable = false, updatable = false)
    private String paymentId;

    @Column(nullable = false)
    private String paymentType; // Momo, ZaloPay, ...

    @Column
    private String paymentInfo;

    @Column(nullable = false)
    private Long amount;

    /**
     * @see <a href="https://developers.momo.vn/v1/#cong-thanh-toan-momo-bang-ma-loi">Momo status code</a>
     */
    @Column(nullable = false)
    private Integer status;

    @Column
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private StudentEntity owner;
}

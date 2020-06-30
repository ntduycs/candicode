package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "password_updates")
public class PasswordUpdateEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long passwordUpdateId;

    @Column(nullable = false)
    private String oldPassword;

    @Column(nullable = false)
    private String newPassword;

    @Column(nullable = false, updatable = false)
    private Long expiredIn; // in minutes

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "user_fkey"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;
}

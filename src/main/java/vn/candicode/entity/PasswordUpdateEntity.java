package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;

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
}

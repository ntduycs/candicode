package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import vn.candicode.models.enums.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"userId", "roles"}, callSuper = false)
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long userId;

    @NaturalId
    @Column(nullable = false)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    @Column
    private Boolean enable = true;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

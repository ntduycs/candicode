package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
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
@SQLDelete(sql = "update user set deleted_at = now() where id = ?")
@Loader(namedQuery = "findUserEntityByEmail")
@NamedQuery(name = "findUserEntityByEmail", query = "select u from UserEntity u where u.email = ?1 and u.deletedAt is null")
@Where(clause = "deleted_at is null")
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
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

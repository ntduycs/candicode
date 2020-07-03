package vn.candicode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "user_email_idx", columnNames = {"email"})})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
@EqualsAndHashCode(of = {"email"}, callSuper = false)
public class UserEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long userId;

    @NaturalId
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleEntity> roles = new ArrayList<>();

    @Transient
    public boolean isEnabled() {
        return enabled;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addRole(RoleEntity role) {
        UserRoleEntity userRole = new UserRoleEntity(this, role);
        roles.add(userRole);
    }

    public void removeRole(RoleEntity role) {
        for (Iterator<UserRoleEntity> iterator = roles.iterator(); iterator.hasNext(); ) {
            UserRoleEntity userRole = iterator.next();
            if (userRole.getUser().equals(this) && userRole.getRole().equals(role)) {
                iterator.remove();
                userRole.setUser(null);
                userRole.setRole(null);
                break;
            }
        }
    }

    // Only for student
    private String slogan;
    private String facebook;
    private String github;
    private String linkedin;
    private String location;
    private String company;
    private String university;
    private String avatar;
}

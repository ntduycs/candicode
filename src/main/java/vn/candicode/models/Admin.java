package vn.candicode.models;

import lombok.*;
import vn.candicode.models.converters.AdminRoles2StringConverter;
import vn.candicode.models.enums.AdminRole;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admins")
public class Admin extends User {
    @Convert(converter = AdminRoles2StringConverter.class)
    @Column(nullable = false)
    private List<AdminRole> roles;

    public Admin(String email, String password, String firstName, String lastName, List<AdminRole> roles) {
        super(email, password, firstName, lastName);
        this.roles = roles;
    }
}

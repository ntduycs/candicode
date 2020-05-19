package vn.candicode.models;

import lombok.*;
import vn.candicode.models.converters.List2StringConverter;
import vn.candicode.models.enums.AdminRole;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admins")
public class Admin extends User {
    @Convert(converter = List2StringConverter.class)
    @Column(nullable = false)
    private List<AdminRole> roles;

    public Admin(String email, String password, String firstName, String lastName, List<AdminRole> roles) {
        super(email, password, firstName, lastName);
        this.roles = roles;
    }
}

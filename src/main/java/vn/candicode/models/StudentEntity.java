package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Loader;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "students")
@Loader(namedQuery = "findStudentEntityByUserId")
@NamedQuery(name = "findStudentEntityByUserId", query = "select u from StudentEntity u where u.userId = ?1 and u.deletedAt is null")
@NoArgsConstructor
public class StudentEntity extends UserEntity {
    @Column
    private Long gainedPoint = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlanEntity plan;

    public StudentEntity(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

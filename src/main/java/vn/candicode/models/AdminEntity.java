package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Loader;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Table(name = "admins")
@Loader(namedQuery = "findAdminEntityById")
@NamedQuery(name = "findAdminEntityById", query = "select u from AdminEntity u where u.userId = ?1 and u.deletedAt is null")
public class AdminEntity extends UserEntity {

}

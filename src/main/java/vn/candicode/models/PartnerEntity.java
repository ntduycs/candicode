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
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "partners")
@Loader(namedQuery = "findPartnerEntityByUserId")
@NamedQuery(name = "findPartnerEntityByUserId", query = "select u from PartnerEntity u where u.userId = ?1 and u.deletedAt is null")
public class PartnerEntity extends UserEntity {
}

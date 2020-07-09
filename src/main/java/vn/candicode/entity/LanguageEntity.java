package vn.candicode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "languages", uniqueConstraints = {@UniqueConstraint(name = "language_name_idx", columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NaturalIdCache
@EqualsAndHashCode(of = {"name"})
public class LanguageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long languageId;

    @NaturalId
    @Column(nullable = false)
    private String name;
}

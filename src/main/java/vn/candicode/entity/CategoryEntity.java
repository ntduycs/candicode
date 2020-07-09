package vn.candicode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "categories", uniqueConstraints = {@UniqueConstraint(name = "category_name_idx", columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
@EqualsAndHashCode(of = {"name"})
public class CategoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long categoryId;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "bigint default 0")
    private Long numUsed = 0L;

    public CategoryEntity() {
    }

    public CategoryEntity(String name) {
        this.name = name;
        this.numUsed = 0L;
    }
}

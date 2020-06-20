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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long categoryId;

    @NaturalId
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long numUsed = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "parent_category_fk"))
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private CategoryEntity parent;
}

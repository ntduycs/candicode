package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"categoryId"})
@Entity
@Table(name = "categories")
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CategoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long categoryId;

    @Column(nullable = false)
    @NaturalId
    private String text;

    private Integer count = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private List<CategoryEntity> children = new ArrayList<>();

    public void addSubCategory(CategoryEntity category) {
        children.add(category);
        category.setParent(this);
    }

    public void removeSubCategory(CategoryEntity category) {
        children.remove(category);
        category.setParent(null);
    }
}

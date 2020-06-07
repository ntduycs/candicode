package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import vn.candicode.models.enums.CategoryName;

import javax.persistence.*;
import java.io.Serializable;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NaturalId
    private CategoryName text;
}

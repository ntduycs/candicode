package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tutorial_categories", uniqueConstraints = {
    @UniqueConstraint(name = "tutorial_category_idx", columnNames = {"tutorial_id", "category_id"})
})
public class TutorialCategoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long tutorialCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutorial_id", nullable = false, foreignKey = @ForeignKey(name = "tutorial_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TutorialEntity tutorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "category_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

    public TutorialCategoryEntity() {
    }

    public TutorialCategoryEntity(TutorialEntity tutorial, CategoryEntity category) {
        this.tutorial = tutorial;
        this.category = category;
    }
}

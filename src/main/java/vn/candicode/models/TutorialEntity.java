package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import vn.candicode.models.converters.TagConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"tutorialId"}, callSuper = false)
@Entity
@Table(name = "tutorials", indexes = {@Index(name = "tutorials_title_idx", columnList = "title")})
public class TutorialEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long tutorialId;

    @Column(nullable = false)
    private String title;

    @Convert(converter = TagConverter.class)
    private List<String> tags = new ArrayList<>();

    private String banner;

    @Lob
    @Type(type = "text")
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String description;

    private Integer likes = 0;
    private Integer dislikes = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity author;

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorialCommentEntity> comments = new ArrayList<>();

    public void addComment(TutorialCommentEntity comment) {
        comments.add(comment);
        comment.setTutorial(this);
    }

    public void removeComment(TutorialCommentEntity comment) {
        comments.remove(comment);
        comment.setTutorial(null);
    }

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorialCategoryEntity> categories = new ArrayList<>();

    public void addCategory(CategoryEntity categoryEntity) {
        TutorialCategoryEntity entity = new TutorialCategoryEntity(this, categoryEntity);
        categories.add(entity);
    }

    public void removeCategory(CategoryEntity categoryEntity) {
        for (Iterator<TutorialCategoryEntity> iterator = categories.iterator(); iterator.hasNext(); ) {
            TutorialCategoryEntity entity = iterator.next();
            if (entity.getTutorial().equals(this) && entity.getCategory().equals(categoryEntity)) {
                iterator.remove();
                entity.setTutorial(null);
                entity.setCategory(null);
            }
        }
    }
}

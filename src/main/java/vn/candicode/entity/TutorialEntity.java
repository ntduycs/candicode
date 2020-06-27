package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import vn.candicode.converter.TagConverter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tutorials", uniqueConstraints = {@UniqueConstraint(name = "tutorial_title_idx", columnNames = {"title"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
public class TutorialEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long tutorialId;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String title;

    @Column
    private String banner;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "text")
    private String content;

    @Column(nullable = false)
    private String brieflyContent;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer dislikes = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Convert(converter = TagConverter.class)
    private Set<String> tags = new HashSet<>();

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorialCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorialCategoryEntity> categories = new ArrayList<>();

    public void addComment(TutorialCommentEntity comment) {
        comments.add(comment);
        comment.setTutorial(this);
    }

    public void removeComment(TutorialCommentEntity comment) {
        comments.remove(comment);
        comment.setTutorial(null);
    }

    public void addCategory(CategoryEntity category) {
        TutorialCategoryEntity challengeCategory = new TutorialCategoryEntity(this, category);
        categories.add(challengeCategory);
    }

    public void removeCategory(CategoryEntity category) {
        for (Iterator<TutorialCategoryEntity> iterator = categories.iterator(); iterator.hasNext(); ) {
            TutorialCategoryEntity tutorialCategory = iterator.next();
            if (tutorialCategory.getTutorial().equals(this) && tutorialCategory.getCategory().equals(category)) {
                iterator.remove();
                tutorialCategory.setTutorial(null);
                tutorialCategory.setCategory(null);
                break;
            }
        }
    }

}

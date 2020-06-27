package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "comments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "comment_type", discriminatorType = DiscriminatorType.STRING)
public class CommentEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer dislikes = 0;

    @Column(nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "parent_comment_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CommentEntity> children = new ArrayList<>();
}

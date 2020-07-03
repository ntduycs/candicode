package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("tutorial")
public class TutorialCommentEntity extends CommentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", foreignKey = @ForeignKey(name = "tutorial_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TutorialEntity tutorial;
}

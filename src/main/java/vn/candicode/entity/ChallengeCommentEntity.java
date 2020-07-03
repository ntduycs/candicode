package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("challenge")
public class ChallengeCommentEntity extends CommentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", foreignKey = @ForeignKey(name = "challenge_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;
}

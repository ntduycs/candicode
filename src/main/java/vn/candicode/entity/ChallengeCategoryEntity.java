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
@Table(name = "challenge_categories", uniqueConstraints = {
    @UniqueConstraint(name = "challenge_category_idx", columnNames = {"challenge_id", "category_id"})
})
public class ChallengeCategoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "category_fk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

    public ChallengeCategoryEntity() {
    }

    public ChallengeCategoryEntity(ChallengeEntity challenge, CategoryEntity category) {
        this.challenge = challenge;
        this.category = category;
    }
}

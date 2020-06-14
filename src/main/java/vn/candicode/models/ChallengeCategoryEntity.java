package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"challengeCategoryId"})
@Entity
@Table(name = "challenge_categories", uniqueConstraints = {@UniqueConstraint(columnNames = {"challenge_id", "category_id"})})
public class ChallengeCategoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long challengeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity category;

    public ChallengeCategoryEntity(ChallengeEntity challenge, CategoryEntity category) {
        this.challenge = challenge;
        this.category = category;
    }
}

package vn.candicode.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import vn.candicode.models.converters.TagConverter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "contests", uniqueConstraints = {
    @UniqueConstraint(name = "title_idx", columnNames = {"title"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NaturalIdCache
@EqualsAndHashCode(of = {"title"}, callSuper = false)
public class ContestEntity extends GenericEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long contestId;

    @NaturalId(mutable = true)
    @Column(nullable = false)
    private String title;

    @Convert(converter = TagConverter.class)
    private Set<String> tags = new HashSet<>();

    @Column
    private String banner;

    @Column(nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "text")
    private String content;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime registrationDeadline;

    @Column
    private Integer maxRegister = -1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private UserEntity author;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestRoundEntity> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestRegistrationEntity> registrations = new ArrayList<>();

    public void addRound(ContestRoundEntity round) {
        rounds.add(round);
        round.setContest(this);
    }

    public void removeRound(ContestRoundEntity round) {
        rounds.remove(round);
        round.setContest(null);
    }

    public void addRegistration(StudentEntity student) {
        ContestRegistrationEntity contestRegistration = new ContestRegistrationEntity(student, this);
        registrations.add(contestRegistration);
    }

    public void removeRegistration(StudentEntity student) {
        for (Iterator<ContestRegistrationEntity> iterator = registrations.iterator(); iterator.hasNext(); ) {
            ContestRegistrationEntity registration = iterator.next();
            if (registration.getContest().equals(this) && registration.getStudent().equals(student)) {
                iterator.remove();
                registration.setContest(null);
                registration.setStudent(null);
                break;
            }
        }
    }
}

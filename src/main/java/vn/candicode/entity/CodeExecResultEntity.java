package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "code_exec_results")
public class CodeExecResultEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long codeExecResultId;

    @ManyToOne
    @JoinColumn(name = "challenge_config_id", nullable = false, foreignKey = @ForeignKey(name = "challenge_config_fk"))
    private ChallengeConfigurationEntity challenge;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "user_fk"))
    private UserEntity user;

    @Column(nullable = false, updatable = false)
    private String submitAt;

    @Column
    private Boolean compiled = false;

    @Column
    private Double doneWithin; // in minutes

    @Column
    private Double execTime; // in nanoseconds

    @Column(nullable = false)
    private Long point;

    @Column
    private LocalDateTime expiresAt;

    // The following 2 fields helps us to eliminate JOIN clause when constructing submission history
    @Column(columnDefinition = "integer default 0", name = "passed")
    private Integer passedTestcases = 0;

    @Column(columnDefinition = "integer default 0", name = "total")
    private Integer totalTestcases = 0;
}

package vn.candicode.models;

import lombok.*;
import vn.candicode.models.enums.CompileStatus;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@Table(name = "submissions")
public class Submission extends PartialBaseModel {
    @Column()
    private Double completionTime; // In microseconds

    @Column()
    private Double executionTime;

    @Column()
    private Double usedMemory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Coder author;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CompileStatus compileStatus;

    @Column(nullable = false)
    private Integer numPassesTestcases;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "challenge_id")
    private Challenge challenge;
}

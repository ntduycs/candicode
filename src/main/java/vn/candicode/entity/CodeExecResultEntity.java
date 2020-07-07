package vn.candicode.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "code_exec_results")
public class CodeExecResultEntity extends Auditable {
    @EmbeddedId
    private CodeExecResultId compositeId;

    @Column
    private Boolean compiled = false;

    @Column
    private Double doneWithin; // in minutes

    @Column
    private Double execTime; // in nanoseconds

    @Column(nullable = false)
    private Integer point;

    // The following 2 fields helps us to eliminate JOIN clause when constructing submission history
    @Column(columnDefinition = "integer default 0", name = "passed")
    private Integer passedTestcases = 0;

    @Column(columnDefinition = "integer default 0", name = "total")
    private Integer totalTestcases = 0;
}

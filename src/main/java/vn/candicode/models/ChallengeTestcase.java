package vn.candicode.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "testcases")
public class ChallengeTestcase extends BaseModel {
    @Column(nullable = false)
    @NonNull
    private String input;

    @Column(nullable = false, name = "output")
    @NonNull
    private String expectedOutput;

    @Column(nullable = false, name = "public_tc")
    @NonNull
    private boolean publicTestcase;

    @ManyToOne(fetch = FetchType.LAZY)
    private Challenge challenge;
}

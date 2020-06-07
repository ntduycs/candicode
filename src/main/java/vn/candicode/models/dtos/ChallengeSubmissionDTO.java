package vn.candicode.models.dtos;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.models.TestcaseEntity;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class ChallengeSubmissionDTO implements Serializable {
    private Long challengeId;

    private Long authorId;

    private Collection<TestcaseEntity> testcases;

    public ChallengeSubmissionDTO(Long challengeId, Long authorId, Collection<TestcaseEntity> testcases) {
        this.challengeId = challengeId;
        this.authorId = authorId;
        this.testcases = testcases;
    }
}

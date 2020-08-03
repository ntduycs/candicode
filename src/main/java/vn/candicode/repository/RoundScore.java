package vn.candicode.repository;

import lombok.Getter;
import vn.candicode.entity.UserEntity;

@Getter
public class RoundScore {
    private final UserEntity user;
    private final Long score;
    private final Long roundId;
    private final Double time;

    public RoundScore(UserEntity user, Long score, Long roundId, Double time) {
        this.user = user;
        this.score = score;
        this.roundId = roundId;
        this.time = time;
    }
}
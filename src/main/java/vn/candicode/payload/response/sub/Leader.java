package vn.candicode.payload.response.sub;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Leader implements Serializable {
    private Long gainedScore;
    private Long maxScore;
    private Long userId;
    private String fullName;
    private String fistName;
    private String lastName;
    private String avatar;
    private Double time;
    private String submitAt; // null in case of contest

    public Leader() {
    }

    public Leader(Long gainedScore, Long maxScore, Long userId, String fullName, String fistName, String lastName, String avatar, Double time, String submitAt) {
        this.gainedScore = gainedScore;
        this.maxScore = maxScore;
        this.userId = userId;
        this.fullName = fullName;
        this.fistName = fistName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.time = time;
        this.submitAt = submitAt;
    }

    public Leader(Long gainedScore, Long maxScore, Long userId, String fullName, String fistName, String lastName, String avatar, Double time) {
        this.gainedScore = gainedScore;
        this.maxScore = maxScore;
        this.userId = userId;
        this.fullName = fullName;
        this.fistName = fistName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.time = time;
    }
}

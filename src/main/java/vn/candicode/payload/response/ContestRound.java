package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContestRound implements Serializable {
    private Long roundId;
    private String startsAt;
    private String endsAt;
    private String name;
    private List<ContestChallenge> challenges = new ArrayList<>();
}

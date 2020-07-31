package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payload.response.sub.Leader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChallengeLeaderboard implements Serializable {
    private List<Leader> leaders = new ArrayList<>();
}

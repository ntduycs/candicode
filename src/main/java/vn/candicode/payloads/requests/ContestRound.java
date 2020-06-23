package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import java.util.Set;

@Getter
@Setter
public class ContestRound extends GenericRequest {
    private Set<Long> challengeIds;
    private String endsAt;
    private String startsAt;
    private RoundConstraint constraints;
}

package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ContestRound extends GenericRequest {
    private Set<Long> challengeIds;
    private LocalDateTime endsAt;
    private LocalDateTime startsAt;
    private RoundConstraint constraints;
}

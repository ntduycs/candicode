package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContestRound extends GenericRequest {
    private Long challengeId;
    private LocalDateTime endAt;
    private LocalDateTime startAt;
    private Integer scorePercent;
    private Integer attendeePercent;
}

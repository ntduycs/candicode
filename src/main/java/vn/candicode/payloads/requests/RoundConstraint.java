package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundConstraint {
    private Integer scorePercent;
    private Integer attendeePercent;
}

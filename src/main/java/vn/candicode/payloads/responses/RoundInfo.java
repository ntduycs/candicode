package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RoundInfo implements Serializable {
    private String startsAt;
    private String endsAt;
    private List<Long> challenges;
    private Boolean status;
    private Constraint constraints;
}

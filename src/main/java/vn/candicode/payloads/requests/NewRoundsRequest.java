package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.payloads.GenericRequest;

import java.util.List;

@Getter
@Setter
public class NewRoundsRequest extends GenericRequest {
    private List<ContestRound> rounds;
}

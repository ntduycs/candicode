package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Constraint implements Serializable {
    private Integer attendeePercent;
    private Integer scorePercent;
}

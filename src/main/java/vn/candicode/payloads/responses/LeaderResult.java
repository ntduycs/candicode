package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LeaderResult implements Serializable {
    private Integer score;
    private Integer doneWithin;
}

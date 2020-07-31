package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IncomingContest implements Serializable {
    private String name;
    private String banner;
    private Long contestId;
    private Long incoming; // in minutes
}

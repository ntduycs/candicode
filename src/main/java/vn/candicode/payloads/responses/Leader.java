package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Leader implements Serializable {
    private String fullName;
    private String avatar;
    private String slug;
    private List<LeaderResult> details;
}

package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionRequest {
    private String language;
    private String code;
}

package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewSubmissionRequest extends Request {
    private String language;
    private String code;
}

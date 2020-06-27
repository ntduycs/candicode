package vn.candicode.payload.request;

import lombok.Setter;

@Setter
public class NewSubmissionRequest extends Request {
    private String language;
    private String code;
    private Double doneWithin; // in minutes

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public Double getDoneWithin() {
        return doneWithin == null ? 0 : doneWithin;
    }
}

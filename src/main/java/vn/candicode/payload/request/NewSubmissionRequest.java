package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewSubmissionRequest extends Request {
    @NotNull
    private Boolean compiled;
    private Double doneWithin;
    @NotNull
    private Double executionTime;
    @NotNull
    private Integer passed;
    @NotNull
    private Integer total;
    @NotNull
    private String code;
    @NotNull
    private String language;
}

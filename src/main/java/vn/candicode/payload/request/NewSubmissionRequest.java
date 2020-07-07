package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewSubmissionRequest extends Request {
    @NotNull(message = "Field 'compiled' is required but not be given")
    private Boolean compiled;
    private Double doneWithin;
    @NotNull(message = "Field 'executionTime is required but not be given'")
    private Double executionTime;
    @NotNull(message = "Field 'passed' is required but not be given")
    private Integer passed;
    @NotNull(message = "Field 'total' is required but not be given")
    private Integer total;
    @NotBlank(message = "Field 'code' is required but not be given")
    private String code;
    @NotBlank(message = "Field 'language' is required but not be given")
    private String language;
    @NotBlank(message = "Field 'submitAt' is required but not be given")
    private String submitAt;
}

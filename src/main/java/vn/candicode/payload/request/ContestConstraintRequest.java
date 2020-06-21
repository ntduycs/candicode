package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContestConstraintRequest extends Request {
    @NotNull(message = "Field 'attendeePercent' is required but not be given")
    @Min(value = 0, message = "Must equal or greater than 0")
    @Max(value = 100, message = "Must equal or less than 100")
    private Integer attendeePercent = 100;

    @NotNull(message = "Field 'scorePercent' is required but not be given")
    @Min(value = 0, message = "Must equal or greater than 0")
    @Max(value = 100, message = "Must equal or less than 100")
    private Integer scorePercent = 0;

    private Integer maxAttempts = -1;
}

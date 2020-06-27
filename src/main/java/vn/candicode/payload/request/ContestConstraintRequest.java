package vn.candicode.payload.request;

import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public Integer getAttendeePercent() {
        return attendeePercent == null ? 100 : attendeePercent;
    }

    public Integer getScorePercent() {
        return scorePercent == null ? 0 : scorePercent;
    }

    public Integer getMaxAttempts() {
        return maxAttempts == null ? -1 : maxAttempts;
    }
}

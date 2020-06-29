package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RoundRequest extends Request {
    private String name;

    @NotNull(message = "Field 'startsAt' is required but not be given")
    @Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9]).(\\d{3})$")
    // yyyy-MM-dd HH:mm:ss.SSS
    private String startsAt;

    @NotNull(message = "Field 'endsAt' is required but not be given")
    @Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9]).(\\d{3})$")
    // yyyy-MM-dd HH:mm:ss.SSS
    private String endsAt;

    @NotNull(message = "Field 'challenges' is required but not be given")
    @Size(min = 1, message = "Must contain at least 1 challenge")
    private Set<Long> challenges;

    private List<ContestConstraintRequest> constraints;
}

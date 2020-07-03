package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReactionRequest extends Request {
    @NotBlank(message = "Field 'type' is required but not be given")
    private String type;

    @NotNull(message = "Field 'id' is required but not be given")
    private Long id;

    @NotNull(message = "Field 'like' is required but not be given")
    private Boolean like;
}

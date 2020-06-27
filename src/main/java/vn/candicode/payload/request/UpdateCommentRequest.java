package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class UpdateCommentRequest extends Request {
    @NotBlank(message = "Field 'content' is required but not be given")
    private String content;
}

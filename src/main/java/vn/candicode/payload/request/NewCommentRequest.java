package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewCommentRequest extends Request {
    @NotBlank(message = "Field 'content' is required but not be given")
    private String content;

    /**
     * This field is used when user reply an existing comment
     */
    private Long parentId;
}

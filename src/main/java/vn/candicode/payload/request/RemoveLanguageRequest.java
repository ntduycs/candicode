package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RemoveLanguageRequest extends Request {
    @NotBlank(message = "Field 'language' is required but not be given")
    private String language;
}

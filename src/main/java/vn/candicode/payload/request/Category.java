package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class Category {
    @NotNull(message = "Field 'categoryId' is required but not be given")
    private Long categoryId;

    @NotBlank(message = "Field 'name' is required but not be given")
    private String name;
}

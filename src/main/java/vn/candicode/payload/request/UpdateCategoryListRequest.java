package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UpdateCategoryListRequest extends Request {
    @Valid
    @Size(min = 1)
    private Set<Category> categories;
}

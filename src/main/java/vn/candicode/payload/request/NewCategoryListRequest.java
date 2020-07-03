package vn.candicode.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NewCategoryListRequest extends Request {
    private Set<String> categories;
}

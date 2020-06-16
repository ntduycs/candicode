package vn.candicode.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.common.structure.composite.impl.CCCategory;
import vn.candicode.payloads.GenericRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryRequest extends GenericRequest {
    private List<CCCategory> categories = new ArrayList<>();
}

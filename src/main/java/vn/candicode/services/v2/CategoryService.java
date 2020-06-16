package vn.candicode.services.v2;

import vn.candicode.common.structure.composite.impl.CCCategory;
import vn.candicode.payloads.responses.Category;

import java.util.List;

public interface CategoryService {
    void createCategories(List<CCCategory> categories);

    List<Category> getAll();
}

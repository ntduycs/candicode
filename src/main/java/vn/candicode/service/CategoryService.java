package vn.candicode.service;

import vn.candicode.payload.request.NewCategoryListRequest;
import vn.candicode.payload.request.RemoveCategoryListRequest;
import vn.candicode.payload.request.UpdateCategoryListRequest;
import vn.candicode.payload.response.Categories;
import vn.candicode.security.UserPrincipal;

public interface CategoryService {
    void createCategories(NewCategoryListRequest payload, UserPrincipal me);

    void updateCategories(UpdateCategoryListRequest payload, UserPrincipal me);

    void deleteCategories(RemoveCategoryListRequest payload, UserPrincipal me);

    Categories getCategories();
}

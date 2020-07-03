package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.entity.CategoryEntity;
import vn.candicode.payload.response.sub.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class Categories implements Serializable {
    private List<Category> categories = new ArrayList<>();

    public Categories(Collection<CategoryEntity> collection) {
        collection.forEach(entity -> categories.add(new Category(entity.getCategoryId(), entity.getName(), entity.getNumUsed())));
    }
}

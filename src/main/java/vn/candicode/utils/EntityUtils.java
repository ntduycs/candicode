package vn.candicode.utils;

import vn.candicode.models.CategoryEntity;
import vn.candicode.payloads.responses.Category;

import java.util.stream.Collectors;

public class EntityUtils {
    public static Category entity2Dto(CategoryEntity entity) {
        Category dto = new Category();
        dto.setCategoryId(entity.getCategoryId());
        dto.setText(entity.getText());
        dto.setCount(entity.getCount());
        dto.setChildren(entity.getChildren().stream().map(EntityUtils::entity2Dto).collect(Collectors.toList()));
        return dto;
    }
}

package vn.candicode.service;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.entity.CategoryEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.Category;
import vn.candicode.payload.request.NewCategoryListRequest;
import vn.candicode.payload.request.RemoveCategoryListRequest;
import vn.candicode.payload.request.UpdateCategoryListRequest;
import vn.candicode.payload.response.Categories;
import vn.candicode.repository.CategoryRepository;
import vn.candicode.security.UserPrincipal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Map<String, CategoryEntity> existingCategories;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CommonService commonService) {
        this.categoryRepository = categoryRepository;
        this.existingCategories = commonService.getCategories();
    }

    @Override
    @Transactional
    public void createCategories(NewCategoryListRequest payload, UserPrincipal me) {
        for (String category: payload.getCategories()) {
            if (!existingCategories.containsKey(category)) {
                CategoryEntity newCategory = categoryRepository.save(new CategoryEntity(category));
                existingCategories.put(category, newCategory);
            }
        }
    }

    @Override
    @Transactional
    public void updateCategories(UpdateCategoryListRequest payload, UserPrincipal me) {
        Map<Long, CategoryEntity> categoryMapById = existingCategories.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getValue().getCategoryId(), Map.Entry::getValue, (a, b) -> b));

        Map<String, CategoryEntity> categoryMapByName = existingCategories;

        Map<Long, String> categoryNameById = existingCategories.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getValue().getCategoryId(), Map.Entry::getKey, (a, b) -> b));

        for (Category category: payload.getCategories()) {
            // Throw exception if any entry has been duplicated name but has different id
            if (categoryMapByName.containsKey(category.getName())) {
                if (!categoryMapByName.get(category.getName()).getCategoryId().equals(category.getCategoryId())) {
                    throw new BadRequestException("Category has already existing with name " + category.getName());
                }
            } else {
                String updatedCategoryName = categoryNameById.get(category.getCategoryId());

                if (updatedCategoryName == null) {
                    throw new ResourceNotFoundException(CategoryEntity.class, "id", category.getCategoryId());
                }

                CategoryEntity updatedCategory = existingCategories.get(updatedCategoryName);

                updatedCategory.setName(category.getName());

                categoryRepository.save(updatedCategory);

                existingCategories.remove(updatedCategoryName);
                existingCategories.put(category.getName(), updatedCategory);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCategories(RemoveCategoryListRequest payload, UserPrincipal me) {
        Map<Long, String> categoryNameById = existingCategories.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getValue().getCategoryId(), Map.Entry::getKey, (a, b) -> b));

        List<CategoryEntity> removedCategories = existingCategories.values().stream()
            .filter(categoryEntity -> payload.getCategoryIds().contains(categoryEntity.getCategoryId()))
            .collect(Collectors.toList());

        categoryRepository.deleteAll(removedCategories);

        payload.getCategoryIds().forEach(id -> existingCategories.remove(categoryNameById.get(id)));
    }

    @Override
    public Categories getCategories() {
        return new Categories(existingCategories.values());
    }
}

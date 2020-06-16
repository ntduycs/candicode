package vn.candicode.services.v2.impl;

import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.structure.composite.impl.CCCategory;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.models.CategoryEntity;
import vn.candicode.payloads.responses.Category;
import vn.candicode.services.v2.CategoryService;
import vn.candicode.utils.EntityUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        List<CategoryEntity> entities = entityManager.createQuery(
            "select c from CategoryEntity c", CategoryEntity.class).getResultList();

        return entities.stream().map(EntityUtils::entity2Dto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createCategories(List<CCCategory> categories) {
        List<String> existingCategories = entityManager.createQuery(
            "select c from CategoryEntity c", CategoryEntity.class)
            .getResultList().stream()
            .map(CategoryEntity::getText)
            .collect(Collectors.toList());

        Pair<Integer, List<String>> errors = validateCategories(categories, existingCategories);

        if (errors.getFirst() > 0) {
            throw new PersistenceException(String.format("Found %d duplicated categories - (%s)", errors.getFirst(), Joiner.on(",").join(errors.getSecond())));
        }

        for (CCCategory category : categories) {
            CategoryEntity entity = new CategoryEntity();
            entity.setText(category.getText());
            populateSubCategories(entity, category.getChildren());
            entityManager.persist(entity);
        }

        entityManager.flush();

        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private Pair<Integer, List<String>> validateCategories(List<CCCategory> categories, List<String> existingCategories) {
        int numErrors = 0;
        List<String> errors = new ArrayList<>();

        for (CCCategory newCategory : categories) {
            if (existingCategories.contains(newCategory.getText())) {
                numErrors++;
                errors.add(newCategory.getText());
            }
            if (newCategory.getChildren() != null && !newCategory.getChildren().isEmpty()) {
                validateCategories(newCategory.getChildren(), existingCategories, numErrors, errors);
            }
        }

        return Pair.of(numErrors, errors);
    }

    private void validateCategories(List<CCCategory> categories, List<String> existingCategories, int numErrors, List<String> errors) {
        if (categories == null || categories.isEmpty()) return;

        for (CCCategory category : categories) {
            if (existingCategories.contains(category.getText())) {
                numErrors++;
                errors.add(category.getText());
            }
            validateCategories(category.getChildren(), existingCategories, numErrors, errors);
        }
    }

    private void populateSubCategories(CategoryEntity parentCategory, List<CCCategory> children) {
        if (children == null || children.isEmpty()) return;

        for (CCCategory child : children) {
            CategoryEntity subCategory = new CategoryEntity();
            subCategory.setText(child.getText());
            parentCategory.addSubCategory(subCategory);
            entityManager.persist(subCategory);
            populateSubCategories(subCategory, child.getChildren());
        }
    }
}

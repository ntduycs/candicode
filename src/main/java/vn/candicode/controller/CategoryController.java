package vn.candicode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.NewCategoryListRequest;
import vn.candicode.payload.request.RemoveCategoryListRequest;
import vn.candicode.payload.request.UpdateCategoryListRequest;
import vn.candicode.payload.response.Categories;
import vn.candicode.security.CurrentUser;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CategoryService;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class CategoryController extends Controller {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    protected String getPath() {
        return "categories";
    }

    @PostMapping(path = "categories")
    public ResponseEntity<?> createCategories(@RequestBody @Valid NewCategoryListRequest payload, @CurrentUser UserPrincipal me) {
        categoryService.createCategories(payload, me);

        return new ResponseEntity<>(ResponseFactory.build(Map.of(
            "message", "Created categories successfully"
        )), HttpStatus.CREATED);
    }

    @PutMapping(path = "categories")
    public ResponseEntity<?> updateCategories(@RequestBody @Valid UpdateCategoryListRequest payload, @CurrentUser UserPrincipal me) {
        categoryService.updateCategories(payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Updated categories successfully"
        )));
    }

    @DeleteMapping(path = "categories")
    public ResponseEntity<?> deleteCategories(@RequestBody @Valid RemoveCategoryListRequest payload, @CurrentUser UserPrincipal me) {
        categoryService.deleteCategories(payload, me);

        return ResponseEntity.ok(ResponseFactory.build(Map.of(
            "message", "Deleted categories successfully"
        )));
    }

    @GetMapping(path = "categories")
    public ResponseEntity<?> getCategories() {
        Categories categories = categoryService.getCategories();

        return ResponseEntity.ok(ResponseFactory.build(categories));
    }
}

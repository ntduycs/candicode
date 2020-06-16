package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payloads.GenericResponse;
import vn.candicode.payloads.requests.CategoryRequest;
import vn.candicode.payloads.responses.Category;
import vn.candicode.services.v2.CategoryService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
public class CategoryController extends GenericController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(path = "/categories")
    public ResponseEntity<?> createCategories(@RequestBody @Valid CategoryRequest payload) {
        categoryService.createCategories(payload.getCategories());

        return ResponseEntity.created(URI.create(getResourceBasePath())).body(GenericResponse.from(
            Map.of("message", "Created categories successfully"), HttpStatus.CREATED
        ));
    }

    @GetMapping(path = "/categories")
    public ResponseEntity<?> getCategories() {
        List<Category> categories = categoryService.getAll();

        return ResponseEntity.ok(GenericResponse.from(categories));
    }

    @Override
    protected String getResourceBasePath() {
        return "categories";
    }
}

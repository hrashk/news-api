package io.github.hrashk.news.api.categories.web;

import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryNotFoundException;
import io.github.hrashk.news.api.categories.CategoryService;
import io.github.hrashk.news.api.util.BeanCopyUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;
    private final CategoryMapper mapper;

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories(@ParameterObject @PageableDefault Pageable pageable) {
        List<Category> news = service.findAll(pageable);

        return ResponseEntity.ok(mapper.wrap(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category news = mapper.map(id);

        return ResponseEntity.ok(mapper.map(news));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody @Valid UpsertCategoryRequest categoryRequest) {
        Category requested = mapper.map(categoryRequest);
        Category saved = service.addOrReplace(requested);

        CategoryResponse response = mapper.map(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody @Valid UpsertCategoryRequest categoryRequest) {
        try {
            Category category = mapper.map(id);
            Category requested = mapper.map(categoryRequest);
            BeanCopyUtils.copyProperties(requested, category);

            Category saved = service.addOrReplace(category);

            return ResponseEntity.ok(mapper.map(saved));
        } catch (CategoryNotFoundException ex) {
            return addCategory(categoryRequest);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = mapper.map(id);

        service.delete(category);

        return ResponseEntity.noContent().build();
    }
}

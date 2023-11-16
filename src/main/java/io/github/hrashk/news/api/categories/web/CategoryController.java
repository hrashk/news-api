package io.github.hrashk.news.api.categories.web;

import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryNotFoundException;
import io.github.hrashk.news.api.categories.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
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

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category news = mapper.fromId(id);

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody UpsertCategoryRequest authorRequest) {
        Category author = mapper.toCategory(authorRequest);
        Category saved = service.addOrReplace(author);

        CategoryResponse response = mapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody UpsertCategoryRequest request) {
        try {
            Category author = mapper.fromId(id);
            BeanUtils.copyProperties(request, author);

            Category saved = service.addOrReplace(author);

            return ResponseEntity.ok(mapper.toResponse(saved));
        } catch (CategoryNotFoundException ex) {
            return addCategory(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (service.contains(id)) {
            service.removeById(id);

            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }
}

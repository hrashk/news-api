package io.github.hrashk.news.api.categories.web;

import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;
    private final CategoryMapper mapper;

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories() {
        List<Category> news = service.findAll();

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        try {
            Category news = service.findById(id);

            return ResponseEntity.ok(mapper.toResponse(news));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody UpsertCategoryRequest authorRequest) {
        Category author = mapper.toCategory(authorRequest);
        Category saved = service.addOrReplace(author);

        return created(mapper.toResponse(saved));
    }

    private static ResponseEntity<CategoryResponse> created(CategoryResponse response) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody UpsertCategoryRequest request) {
        try {
            Category author = service.findById(id);
            BeanUtils.copyProperties(request, author);

            Category saved = service.addOrReplace(author);

            return ResponseEntity.ok(mapper.toResponse(saved));
        } catch (NoSuchElementException ex) {
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

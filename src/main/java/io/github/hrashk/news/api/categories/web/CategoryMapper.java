package io.github.hrashk.news.api.categories.web;

import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class CategoryMapper {
    @Autowired
    protected CategoryService service;

    abstract List<CategoryResponse> map(Iterable<Category> categories);

    public CategoryListResponse wrap(Iterable<Category> categories) {
        return new CategoryListResponse(map(categories));
    }

    abstract CategoryResponse map(Category category);

    abstract Category map(UpsertCategoryRequest categoryRequest);

    public Category map(Long id) {
        return service.findById(id);
    }
}

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

    abstract List<CategoryResponse> toResponseList(List<Category> news);

    public CategoryListResponse toResponse(List<Category> news) {
        return new CategoryListResponse(toResponseList(news));
    }

    abstract CategoryResponse toResponse(Category news);

    abstract Category toCategory(UpsertCategoryRequest newsRequest);

    public Category fromId(Long id) {
        return service.findById(id);
    }
}

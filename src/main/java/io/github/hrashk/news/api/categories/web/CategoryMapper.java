package io.github.hrashk.news.api.categories.web;

import io.github.hrashk.news.api.categories.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    List<CategoryResponse> toResponseList(List<Category> news);

    default CategoryListResponse toResponse(List<Category> news) {
        return new CategoryListResponse(toResponseList(news));
    }

    CategoryResponse toResponse(Category news);

    Category toCategory(UpsertCategoryRequest newsRequest);
}

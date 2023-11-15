package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.news.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {
    List<NewsResponse> toResponseList(List<News> news);

    default NewsListResponse toResponse(List<News> news) {
        return new NewsListResponse(toResponseList(news));
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    NewsResponse toResponse(News news);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    News toNews(UpsertNewsRequest newsRequest);

    default Author authorFromId(Long id) {
        return Author.builder()
                .id(id)
                .build();
    }

    default Category categoryFromId(Long id) {
        return Category.builder()
                .id(id)
                .build();
    }
}

package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    List<NewsResponse> toResponseList(List<News> news);

    default NewsListResponse toResponse(List<News> news) {
        return new NewsListResponse(toResponseList(news));
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    NewsResponse toResponse(News news);

    News toNews(UpsertNewsRequest newsRequest);
}

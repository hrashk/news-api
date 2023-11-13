package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    List<NewsResponse> toResponseList(List<News> news);

    default NewsListResponse toResponse(List<News> news) {
        return new NewsListResponse(toResponseList(news));
    }
}

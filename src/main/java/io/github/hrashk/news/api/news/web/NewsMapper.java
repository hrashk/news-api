package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.authors.web.AuthorMapper;
import io.github.hrashk.news.api.categories.web.CategoryMapper;
import io.github.hrashk.news.api.comments.web.CommentMapper;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AuthorMapper.class, CategoryMapper.class, CommentMapper.class})
public abstract class NewsMapper {
    @Autowired
    protected NewsService service;

    public News fromId(Long id) {
        return service.findById(id);
    }

    abstract List<NewsResponse> toResponseList(List<News> news);

    public NewsListResponse toResponse(List<News> news) {
        return new NewsListResponse(toResponseList(news));
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    abstract NewsResponse toResponse(News news);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    abstract News toNews(UpsertNewsRequest newsRequest);
}

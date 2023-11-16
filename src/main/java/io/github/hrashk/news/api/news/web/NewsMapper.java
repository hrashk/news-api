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

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "commentsCount", expression = "java( news.getComments() == null ? 0 : news.getComments().size() )")
    abstract NewsWithCountResponse toCountResponse(News news);

    abstract List<NewsWithCountResponse> toCountResponseList(List<News> news);

    public NewsListResponse toResponse(List<News> news) {
        return new NewsListResponse(toCountResponseList(news));
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    abstract NewsResponse toResponse(News news);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    abstract News toNews(UpsertNewsRequest newsRequest);
}

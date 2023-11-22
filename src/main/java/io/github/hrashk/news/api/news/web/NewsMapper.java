package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.authors.web.AuthorMapper;
import io.github.hrashk.news.api.categories.web.CategoryMapper;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.comments.web.CommentResponse;
import io.github.hrashk.news.api.comments.web.UpsertCommentRequest;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * Also includes the Comment mapper to avoid circular references
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AuthorMapper.class, CategoryMapper.class})
public abstract class NewsMapper {
    @Autowired
    protected NewsService service;
    @Autowired
    protected CommentService commentService;

    public News mapToNews(Long id) {
        return service.findById(id);
    }

    public Comment mapToComment(Long id) {
        return commentService.findById(id);
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "commentsCount", expression = "java( news.getComments() == null ? 0 : news.getComments().size() )")
    public abstract NewsWithCountResponse mapToCount(News news);

    public abstract List<NewsWithCountResponse> toNewsList(Collection<News> news);

    public NewsListResponse wrap(Collection<News> news) {
        return new NewsListResponse(toNewsList(news));
    }

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract NewsResponse map(News news);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    public abstract News map(UpsertNewsRequest newsRequest);

    public abstract List<CommentResponse> toCommentList(Collection<Comment> comments);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "newsId", source = "news.id")
    public abstract CommentResponse map(Comment comment);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "news", source = "newsId")
    public abstract Comment map(UpsertCommentRequest commentRequest);
}

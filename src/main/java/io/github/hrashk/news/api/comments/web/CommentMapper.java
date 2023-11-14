package io.github.hrashk.news.api.comments.web;

import io.github.hrashk.news.api.comments.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    List<CommentResponse> toResponseList(List<Comment> news);

    default CommentListResponse toResponse(List<Comment> news) {
        return new CommentListResponse(toResponseList(news));
    }

    CommentResponse toResponse(Comment news);

    Comment toComment(UpsertCommentRequest newsRequest);
}

package io.github.hrashk.news.api.comments.web;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class CommentMapper {
    @Autowired
    protected CommentService service;

    abstract List<CommentResponse> toResponseList(List<Comment> comments);

    public CommentListResponse toResponse(List<Comment> comments) {
        return new CommentListResponse(toResponseList(comments));
    }

    abstract CommentResponse toResponse(Comment comment);

    abstract Comment toComment(UpsertCommentRequest commentRequest);

    public Comment fromId(Long id) {
        return service.findById(id);
    }
}

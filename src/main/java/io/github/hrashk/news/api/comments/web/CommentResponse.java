package io.github.hrashk.news.api.comments.web;

public record CommentResponse(Long id, Long newsId, Long authorId, String text) {
}

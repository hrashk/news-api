package io.github.hrashk.news.api.comments.web;

public record UpsertCommentRequest(Long newsId, Long authorId, String text) {
}

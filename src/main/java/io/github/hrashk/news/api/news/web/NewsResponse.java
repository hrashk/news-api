package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.comments.web.CommentResponse;

import java.util.List;

public record NewsResponse(Long id, Long authorId, Long categoryId,
                           String headline, String content, List<CommentResponse> comments) {
}

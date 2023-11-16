package io.github.hrashk.news.api.news.web;

public record NewsWithCountResponse(Long id, Long authorId, Long categoryId,
                                    String headline, String content, int commentsCount) {
}

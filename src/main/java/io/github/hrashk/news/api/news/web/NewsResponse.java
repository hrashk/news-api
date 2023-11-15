package io.github.hrashk.news.api.news.web;

public record NewsResponse(Long id, Long authorId, Long categoryId, String headline, String content) {
}

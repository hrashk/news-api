package io.github.hrashk.news.api.news.web;

public record UpsertNewsRequest(Long authorId, Long categoryId, String headline, String content) {
}

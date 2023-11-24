package io.github.hrashk.news.api.news.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertNewsRequest(
        @NotNull Long authorId,
        @NotNull Long categoryId,
        @NotBlank String headline,
        @NotBlank String content) {
}

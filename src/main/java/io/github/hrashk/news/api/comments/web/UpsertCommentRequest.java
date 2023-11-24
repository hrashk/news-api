package io.github.hrashk.news.api.comments.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertCommentRequest(
        @NotNull Long newsId,
        @NotNull Long authorId,
        @NotBlank String text) {
}

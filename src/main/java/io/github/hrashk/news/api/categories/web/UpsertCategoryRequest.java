package io.github.hrashk.news.api.categories.web;

import jakarta.validation.constraints.NotBlank;

public record UpsertCategoryRequest(@NotBlank String name) {
}

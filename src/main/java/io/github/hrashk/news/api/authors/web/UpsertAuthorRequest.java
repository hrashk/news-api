package io.github.hrashk.news.api.authors.web;

import jakarta.validation.constraints.NotBlank;

public record UpsertAuthorRequest(
        @NotBlank String firstName,
        @NotBlank String lastName) {
}

package io.github.hrashk.news.api.authors;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record AuthorResponse(Long id, String firstName, String lastName, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
}

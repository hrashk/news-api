package io.github.hrashk.news.api.authors;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AuthorResponse(Long id, String firstName, String lastName, Instant createdAt, Instant updatedAt) {
    public static final AuthorResponse EMPTY = builder().build();
}

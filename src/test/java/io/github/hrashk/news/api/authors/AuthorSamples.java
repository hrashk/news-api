package io.github.hrashk.news.api.authors;

import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

@TestComponent
public class AuthorSamples {
    public String baseUrl() {
        return "/api/v1/authors";
    }

    public Long validId() {
        return 3L;
    }

    public Long invalidId() {
        return 713L;
    }

    public String validAuthorUrl() {
        return baseUrl() + "/" + validId();
    }

    public String invalidAuthorUrl() {
        return baseUrl() + "/" + invalidId();
    }

    public List<Author> twoAuthors() {
        var a1 = Author.builder()
                .id(validId())
                .firstName("Holy")
                .lastName("Moly")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
        var a2 = Author.builder()
                .id(13L)
                .firstName("Syster")
                .lastName("Pauly")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
        return List.of(a1, a2);
    }

    public List<Author> twoNewAuthors() {
        var authors = twoAuthors();

        authors.forEach(a -> a.setId(null));

        return authors;
    }

    public Author jackDoe() {
        return Author.builder()
                .id(validId())
                .firstName("Jack")
                .lastName("Doe")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public Author withoutId() {
        return Author.builder()
                .firstName("Jack")
                .lastName("Doe")
                .build();
    }

    public Author withInvalidId() {
        return Author.builder()
                .id(invalidId())
                .firstName("Jack")
                .lastName("Doe")
                .build();
    }
}

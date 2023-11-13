package io.github.hrashk.news.api.authors;

import java.time.LocalDateTime;
import java.util.List;

public class AuthorSamples {
    static List<Author> twoAuthors() {
        var a1 = Author.builder()
                .id(3L)
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

    static Author jackDoe() {
        return Author.builder()
                .id(3L)
                .firstName("Jack")
                .lastName("Doe")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    static Author withoutId() {
        return Author.builder()
                .firstName("Jack")
                .lastName("Doe")
                .build();
    }

    static Author withId() {
        return Author.builder()
                .id(123123L)
                .firstName("Jack")
                .lastName("Doe")
                .build();
    }
}

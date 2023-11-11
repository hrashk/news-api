package io.github.hrashk.news.api.authors;

import java.time.Instant;
import java.util.List;

public class TestData {
    static List<Author> twoAuthors() {
        var a1 = Author.builder()
                .id(3L)
                .firstName("Holy")
                .lastName("Moly")
                .createdAt(Instant.parse("2011-10-13T00:00:00.00Z"))
                .updatedAt(Instant.parse("2011-10-13T00:00:00.00Z"))
                .build();
        var a2 = Author.builder()
                .id(13L)
                .firstName("Syster")
                .lastName("Pauly")
                .createdAt(Instant.parse("2011-07-17T00:00:00.00Z"))
                .updatedAt(Instant.parse("2011-08-21T00:00:00.00Z"))
                .build();
        return List.of(a1, a2);
    }
}

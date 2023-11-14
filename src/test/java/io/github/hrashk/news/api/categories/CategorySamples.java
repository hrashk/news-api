package io.github.hrashk.news.api.categories;

import java.time.LocalDateTime;
import java.util.List;

public final class CategorySamples {

    static List<Category> twoCategories() {
        Category n1 = sciFi();

        Category n2 = Category.builder()
                .id(22L)
                .name("Romance")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        return List.of(n1, n2);
    }

    public static Category sciFi() {
        return Category.builder()
                .id(7L)
                .name("Science Fiction")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public static Category withoutId() {
        return Category.builder()
                .name("Science Fiction")
                .build();
    }

    public static Category withId() {
        return Category.builder()
                .id(123123L)
                .name("Science Fiction")
                .build();
    }
}

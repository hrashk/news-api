package io.github.hrashk.news.api.categories;

import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

@TestComponent
public class CategorySamples {
    public String baseUrl() {
        return "/api/v1/categories";
    }

    public Long validId() {
        return 7L;
    }

    public Long invalidId() {
        return 522L;
    }

    public String validCategoryUrl() {
        return baseUrl() + "/" + validId();
    }

    public String invalidCategoryUrl() {
        return baseUrl() + "/" + invalidId();
    }


    public List<Category> twoCategories() {
        Category n1 = sciFi();

        Category n2 = Category.builder()
                .id(22L)
                .name("Romance")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        return List.of(n1, n2);
    }

    public List<Category> twoNewCategories() {
        var cats = twoCategories();

        cats.forEach(c -> c.setId(null));
        return cats;
    }

    public Category sciFi() {
        return Category.builder()
                .id(validId())
                .name("Science Fiction")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public Category withoutId() {
        return Category.builder()
                .name("Science Fiction")
                .build();
    }

    public Category withInvalidId() {
        return Category.builder()
                .id(invalidId())
                .name("Science Fiction")
                .build();
    }
}

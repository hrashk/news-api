package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.categories.Category;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestComponent
public class NewsSamples {
    public String baseUrl() {
        return "/api/v1/news";
    }

    public Long validId() {
        return 7L;
    }

    public Long invalidId() {
        return 522L;
    }

    public String validIdUrl() {
        return baseUrl() + "/" + validId();
    }

    public String invalidIdUrl() {
        return baseUrl() + "/" + invalidId();
    }

    public List<News> twoNews() {
        News n1 = greatNews();

        News n2 = sadNews();

        return List.of(n1, n2);
    }

    public News greatNews() {
        return News.builder()
                .id(validId())
                .author(Author.builder().id(3L).build())
                .category(Category.builder().id(11L).build())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public News sadNews() {
        return News.builder()
                .id(22L)
                .author(Author.builder().id(5L).build())
                .category(Category.builder().id(13L).build())
                .headline("Sad news")
                .content("Dolor sit amet")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public News withoutId() {
        return News.builder()
                .author(Author.builder().id(3L).build())
                .category(Category.builder().id(11L).build())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }

    public News withInvalidId() {
        return News.builder()
                .id(invalidId())
                .author(Author.builder().id(3L).build())
                .category(Category.builder().id(11L).build())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }

    public void assertAreSimilar(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual.getAuthor().getId()).as("Author id").isEqualTo(expected.getAuthor().getId()),
                () -> assertThat(actual.getCategory().getId()).as("Category id").isEqualTo(expected.getCategory().getId()),
                () -> assertThat(actual.getHeadline()).as("Headline").isEqualTo(expected.getHeadline()),
                () -> assertThat(actual.getContent()).as("Content").isEqualTo(expected.getContent())
        );
    }

    public void assertHaveSameAuditDates(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual.getCreatedAt()).as("Created at").isEqualTo(expected.getCreatedAt()),
                () -> assertThat(actual.getUpdatedAt()).as("Updated at").isEqualTo(expected.getUpdatedAt())
        );
    }

    public void assertHaveSameIds(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual).hasFieldOrPropertyWithValue("id", expected.getId())
        );
    }

    public void assertIdIsNull(News news) {
        Assertions.assertAll(
                () -> assertThat(news).hasFieldOrPropertyWithValue("id", null)
        );
    }
}

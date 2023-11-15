package io.github.hrashk.news.api.news;

import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

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

        News n2 = News.builder()
                .id(22L)
                .headline("Sad news")
                .content("Dolor sit amet")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        return List.of(n1, n2);
    }

    public News greatNews() {
        return News.builder()
                .id(validId())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public News withoutId() {
        return News.builder()
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }

    public News withInvalidId() {
        return News.builder()
                .id(invalidId())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }
}

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.news.web.UpsertNewsRequest;

import java.time.LocalDateTime;
import java.util.List;

public final class NewsSamples {

    static List<News> twoNews() {
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

    public static News greatNews() {
        return News.builder()
                .id(7L)
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public static News withoutId() {
        return News.builder()
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }

    public static News withId() {
        return News.builder()
                .id(123123L)
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .build();
    }

    public static UpsertNewsRequest greatNewsRequest() {
        return new UpsertNewsRequest("Great news", "Lorem ipsum dolor");
    }
}

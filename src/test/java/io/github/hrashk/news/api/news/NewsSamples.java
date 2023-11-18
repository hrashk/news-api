package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.comments.CommentSamples;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

@TestComponent
@Import(CommentSamples.class)
@RequiredArgsConstructor
public class NewsSamples {
    private final CommentSamples commentSamples;

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

    public List<News> twoNewsWithNewDependencies() {
        List<News> news = twoNews();
        news.forEach(n -> {
            n.setAuthor(null);
            n.setCategory(null);
            n.setComments(null);
        });

        return news;
    }

    public News greatNews() {
        return News.builder()
                .id(validId())
                .author(Author.builder().id(3L).build())
                .category(Category.builder().id(11L).build())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .comments(commentSamples.twoComments())
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
                .author(Author.builder().build())
                .category(Category.builder().build())
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
}

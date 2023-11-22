package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.news.News;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class EntitySamples {
    public List<News> twoNews() {
        return List.of(greatNews(), sadNews());
    }

    public News greatNews() {
        News news = new News().toBuilder()
                .id(1L)
                .category(sciFi())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();

        twoComments(news);
        jackDoe().addNews(news);

        return news;
    }

    public News sadNews() {
        News news = new News().toBuilder()
                .id(2L)
                .category(romance())
                .headline("Sad news")
                .content("Dolor sit amet")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        hollyMolly().addNews(news);

        return news;
    }


    public List<Comment> twoComments(News news) {
        return List.of(smiley(news), smirky(news));
    }

    public Comment smiley(News news) {
        Comment comment = Comment.builder()
                .id(3L)
                .text("Smiley comment")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();

        hollyMolly().addComment(comment);
        news.addComment(comment);

        return comment;
    }

    public Comment smirky(News news) {
        Comment comment = Comment.builder()
                .id(5L)
                .text("Simrky comment")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        janeSmith().addComment(comment);
        news.addComment(comment);

        return comment;
    }

    public List<Category> twoCategories() {
        return List.of(sciFi(), romance());
    }

    public Category sciFi() {
        return new Category().toBuilder()
                .id(8L)
                .name("Science Fiction")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public Category romance() {
        return new Category().toBuilder()
                .id(13L)
                .name("Romance")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public List<Author> twoAuthors() {
        return List.of(jackDoe(), hollyMolly());
    }

    public Author jackDoe() {
        return new Author().toBuilder()
                .id(21L)
                .firstName("Jack")
                .lastName("Doe")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public Author hollyMolly() {
        return new Author().toBuilder()
                .id(34L)
                .firstName("Holly")
                .lastName("Molly")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public Author janeSmith() {
        return new Author().toBuilder()
                .id(55L)
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }
}

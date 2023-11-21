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
                .id(7L)
                .category(sciFi())
                .headline("Great news")
                .content("Lorem ipsum dolor")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();

        List<Comment> comments = twoComments();
        Author author = new Author().toBuilder().id(3L).build();

        comments.forEach(news::addComment);
        comments.forEach(author::addComment);
        author.addNews(news);

        return news;
    }

    public News sadNews() {
        News news = new News().toBuilder()
                .id(22L)
                .category(romance())
                .headline("Sad news")
                .content("Dolor sit amet")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        Author author = new Author().toBuilder().id(5L).build();
        author.addNews(news);

        return news;
    }


    public List<Comment> twoComments() {
        return List.of(smiley(), smirky());
    }

    public Comment smiley() {
        return Comment.builder()
                .id(7L)
                .text("Smiley comment")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public Comment smirky() {
        return Comment.builder()
                .id(22L)
                .text("Simrky comment")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    public List<Category> twoCategories() {
        return List.of(sciFi(), romance());
    }

    public Category sciFi() {
        return new Category().toBuilder()
                .id(7L)
                .name("Science Fiction")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public Category romance() {
        return new Category().toBuilder()
                .id(22L)
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
                .id(3L)
                .firstName("Jack")
                .lastName("Doe")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }

    private static Author hollyMolly() {
        return new Author().toBuilder()
                .id(13L)
                .firstName("Holly")
                .lastName("Molly")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }
}

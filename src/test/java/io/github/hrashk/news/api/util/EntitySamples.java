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
        News n1 = greatNews();

        News n2 = sadNews();

        return List.of(n1, n2);
    }

    public News greatNews() {
        News news = new News().toBuilder()
                .id(7L)
                .category(Category.builder().id(11L).build())
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
        return new News().toBuilder()
                .id(22L)
                .author(Author.builder().id(5L).build())
                .category(Category.builder().id(13L).build())
                .headline("Sad news")
                .content("Dolor sit amet")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }


    public List<Comment> twoComments() {
        Comment n1 = smiley(7L);

        Comment n2 = smirky(22L);

        return List.of(n1, n2);
    }

    public Comment smiley(Long id) {
        return Comment.builder()
                .id(id)
                .text("Smiley comment")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public Comment smirky(Long id) {
        return Comment.builder()
                .id(id)
                .text("Simrky comment")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();
    }
}

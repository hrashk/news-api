package io.github.hrashk.news.api.comments;

import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDateTime;
import java.util.List;

@TestComponent
public class CommentSamples {
    public long invalidId() {
        return 123123L;
    }


    public List<Comment> twoComments() {
        Comment n1 = smiley();

        Comment n2 = smirky();

        return List.of(n1, n2);
    }

    public List<Comment> twoNewComments() {
        var comments = twoComments();

        comments.forEach(c -> c.setId(null));

        return comments;
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

    public Comment withoutId() {
        return Comment.builder()
                .text("Smiley comments")
                .build();
    }

    public Comment withId() {
        return Comment.builder()
                .id(invalidId())
                .text("Smiley comments")
                .build();
    }
}

package io.github.hrashk.news.api.comment;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.web.UpsertCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

public final class CommentSamples {

    static List<Comment> twoComments() {
        Comment n1 = smiley();

        Comment n2 = Comment.builder()
                .id(22L)
                .text("Simrky comment")
                .createdAt(LocalDateTime.parse("2011-07-17T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-08-21T00:00:00"))
                .build();

        return List.of(n1, n2);
    }

    public static Comment smiley() {
        return Comment.builder()
                .id(7L)
                .text("Smiley comment")
                .createdAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .updatedAt(LocalDateTime.parse("2011-10-13T00:00:00"))
                .build();
    }

    public static Comment withoutId() {
        return Comment.builder()
                .text("Smiley comment")
                .build();
    }

    public static Comment withId() {
        return Comment.builder()
                .id(123123L)
                .text("Smiley comment")
                .build();
    }

    public static UpsertCommentRequest smileyRequest() {
        return new UpsertCommentRequest("Smiley comment");
    }
}

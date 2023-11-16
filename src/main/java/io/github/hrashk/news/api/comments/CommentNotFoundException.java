package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(Long id) {
        super("Comment", id);
    }
}

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;

public class NewsNotFoundException extends EntityNotFoundException {
    public NewsNotFoundException(Long id) {
        super("News", id);
    }
}

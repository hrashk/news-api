package io.github.hrashk.news.api.authors;


import io.github.hrashk.news.api.exceptions.EntityNotFoundException;

public class AuthorNotFoundException extends EntityNotFoundException {
    public AuthorNotFoundException(Long id) {
        super("Author", id);
    }
}

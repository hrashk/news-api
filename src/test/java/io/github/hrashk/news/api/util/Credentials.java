package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;

public record Credentials(String username, String password) {
    public Credentials(Author author) {
        this(author.getUsername(), author.getPassword());
    }
}

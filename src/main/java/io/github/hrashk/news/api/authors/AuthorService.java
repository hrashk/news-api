package io.github.hrashk.news.api.authors;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    public List<Author> findAll() {
        return List.of(Author.builder().build(), Author.builder().build());
    }
}

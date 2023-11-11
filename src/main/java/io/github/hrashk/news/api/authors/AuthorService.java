package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Author save(Author author) {
        return author;
    }
}

package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorsRepository repository;

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Author add(Author author) {
        return repository.save(author);
    }

    /**
     * @throws java.util.NoSuchElementException if id is not found
     */
    public Author findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
}

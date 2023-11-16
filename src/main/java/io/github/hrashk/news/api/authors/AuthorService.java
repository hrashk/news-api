package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;

    public List<Author> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public Author addOrReplace(Author author) {
        return repository.save(author);
    }

    public Author findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
    }

    public boolean contains(Long id) {
        return repository.existsById(id);
    }

    public void removeById(Long id) {
        repository.deleteById(id);
    }
}

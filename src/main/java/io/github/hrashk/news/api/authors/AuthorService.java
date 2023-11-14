package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;

    public List<Author> findAll(int pageNumber, int pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize)).getContent();
    }

    public Author addOrReplace(Author author) {
        return repository.save(author);
    }

    /**
     * @throws java.util.NoSuchElementException if id is not found
     */
    public Author findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public boolean contains(Long id) {
        return repository.existsById(id);
    }

    public void removeById(Long id) {
        repository.deleteById(id);
    }
}

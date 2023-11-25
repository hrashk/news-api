package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.CrudService;
import io.github.hrashk.news.api.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService implements CrudService<Author, Long> {
    private final AuthorRepository repository;

    public List<Author> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public Author findById(Long id) throws AuthorNotFoundException {
        return repository.findById(id).orElseThrow(() -> new AuthorNotFoundException(id));
    }

    @Override
    public void replaceById(Long id, Author entity) throws AuthorNotFoundException {
        var current = findById(id);
        BeanCopyUtils.copyProperties(entity, current);

        repository.save(current);
    }

    @Override
    public Long add(Author entity) {
        var saved = repository.save(entity);

        return saved.getId();
    }

    @Override
    public void deleteById(Long id) throws AuthorNotFoundException {
        var author = findById(id);

        repository.delete(author);
    }
}

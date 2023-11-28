package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.common.BaseService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService extends BaseService<Author, AuthorRepository> {
    public AuthorService(AuthorRepository repository) {
        super(repository, "Author");
    }

    public List<Author> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }
}

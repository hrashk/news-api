package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.aspects.SameAuthor;
import io.github.hrashk.news.api.common.BaseService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService extends BaseService<Author, AuthorRepository> {
    private final PasswordEncoder passwordEncoder;

    public AuthorService(AuthorRepository repository, PasswordEncoder passwordEncoder) {
        super(repository, "Author");
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @SameAuthor
    public Author findById(Long id) throws EntityNotFoundException {
        return super.findById(id);
    }

    @Override
    public Long add(Author author) {
        encodePassword(author);

        return super.add(author);
    }

    public void encodePassword(Author author) {
        author.setPassword(passwordEncoder.encode(author.getPassword()));
    }

    public List<Author> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public Author findByUsername(String username) {
        return repository.findByUsername(username);
    }
}

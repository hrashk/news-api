package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import(AuthorService.class)
class AuthorServiceTest {
    private static final long INVALID_ID = 11333L;
    @Autowired
    private AuthorService service;
    @Autowired
    private AuthorsRepository repository;

    private List<Author> savedEntities;


    @BeforeEach
    void insertAuthors() {
        savedEntities = repository.saveAll(AuthorSamples.twoAuthors());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void saveWithNullId() {
        var a = AuthorSamples.withoutId();

        Author saved = service.addOrReplace(a);

        assertThat(saved.getId()).as("Author id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var a = AuthorSamples.withId();
        long originalId = a.getId();  // the author object is changed after saving

        Author saved = service.addOrReplace(a);

        assertThat(saved.getId()).as("Author id").isNotEqualTo(originalId);
    }

    @Test
    void findByValidId() {
        Author expected = savedEntities.get(0);

        Author actual = service.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(INVALID_ID))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void containsValidId() {
        Author first = savedEntities.get(0);

        assertThat(service.contains(first.getId())).isTrue();
    }

    @Test
    void doesNotContainInvalidId() {
        assertThat(service.contains(INVALID_ID)).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedEntities.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}

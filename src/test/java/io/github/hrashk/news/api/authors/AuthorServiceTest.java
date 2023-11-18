package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({AuthorService.class, AuthorSamples.class})
class AuthorServiceTest {
    @Autowired
    private AuthorService service;
    @Autowired
    private AuthorRepository repository;
    @Autowired
    protected AuthorSamples samples;

    private List<Author> savedEntities;


    @BeforeEach
    void insertAuthors() {
        savedEntities = repository.saveAll(samples.twoAuthors());
    }

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 1))).hasSize(1);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 1))).hasSize(1);
    }

    @Test
    void saveWithNullId() {
        var a = samples.withoutId();

        Author saved = service.addOrReplace(a);

        assertThat(saved.getId()).as("Author id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var a = samples.withInvalidId();
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
        assertThatThrownBy(() -> service.findById(samples.invalidId()))
                .isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    void removeById() {
        Long id = savedEntities.get(0).getId();

        service.delete(savedEntities.get(0));

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(AuthorNotFoundException.class);
    }
}

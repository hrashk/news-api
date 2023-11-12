package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerJpaTest
@Import(AuthorService.class)
class AuthorServiceTest {
    @Autowired
    private AuthorService service;
    @Autowired
    private AuthorRepository repository;

    private List<Author> savedEntities;


    @BeforeEach
    void insertAuthors() {
        savedEntities = repository.saveAll(TestData.twoAuthors());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void saveNew() {
        var a = Author.builder()
                .firstName("Jack")
                .lastName("Doe")
                .build();

        Author saved = service.save(a);

        assertThat(saved.getId()).as("Author id").isNotNull();
    }

    @Test
    void findById() {
        Author expected = savedEntities.get(0);

        Author actual = service.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }
}

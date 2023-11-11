package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.PostgreSQLInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
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

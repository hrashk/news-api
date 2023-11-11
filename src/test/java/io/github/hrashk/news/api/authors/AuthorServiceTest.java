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


    @BeforeEach
    void insertAuthors() {
        repository.saveAll(TestData.twoAuthors());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }
}

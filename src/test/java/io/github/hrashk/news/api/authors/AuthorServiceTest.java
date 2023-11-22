package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.util.ContainerJpaTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({AuthorService.class, DataSeeder.class})
class AuthorServiceTest {
    @Autowired
    private AuthorService service;
    @Autowired
    private DataSeeder seeder;

    private List<Author> savedEntities;

    @BeforeEach
    void seedAuthors() {
        seeder.seed(5);
        savedEntities = seeder.authors();
    }

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 3))).hasSize(3);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 2))).hasSize(2);
    }

    @Test
    void saveWithNullId() {
        Author saved = service.addOrReplace(seeder.aRandomAuthor(-1L));

        assertThat(saved.getId()).as("Author id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var a = seeder.aRandomAuthor(-1L);
        a.setId(-1L);

        Author saved = service.addOrReplace(a);

        assertThat(saved.getId()).as("Author id").isGreaterThan(0);
    }

    @Test
    void findByValidId() {
        Author expected = savedEntities.get(0);

        Author actual = service.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    void deleteWithNews() {
        Author author = seeder.news().get(0).getAuthor();
        Long id = author.getId();

        service.delete(author);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    void deleteWithComments() {
        Author author = seeder.comments().get(0).getAuthor();
        Long id = author.getId();

        service.delete(author);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(AuthorNotFoundException.class);
    }
}

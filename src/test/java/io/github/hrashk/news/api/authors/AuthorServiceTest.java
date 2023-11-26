package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.util.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(AuthorService.class)
class AuthorServiceTest extends ServiceTest {
    @Autowired
    private AuthorService service;

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 3))).hasSize(3);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 2))).hasSize(2);
    }

    @Test
    void add() {
        Long id = service.add(seeder.aRandomAuthor(-1L));

        assertThat(id).as("Author id").isNotNull();
    }

    @Test
    void update() {
        Author author = seeder.authors().get(1);
        author.setFirstName("asdf");

        service.updateOrAdd(author.getId(), author);

        assertThat(service.findById(author.getId())).hasFieldOrPropertyWithValue("firstName", "asdf");
    }

    @Test
    void findByValidId() {
        Author expected = seeder.authors().get(0);

        Author actual = service.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteWithNews() {
        Author author = seeder.news().get(0).getAuthor();
        Long id = author.getId();

        service.deleteById(id);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteWithComments() {
        Author author = seeder.comments().get(0).getAuthor();
        Long id = author.getId();

        service.deleteById(id);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

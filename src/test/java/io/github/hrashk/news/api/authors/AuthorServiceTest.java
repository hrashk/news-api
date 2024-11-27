package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.security.RoleType;
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
        seeder.flush();

        assertThat(id).as("Author id").isNotNull();
    }

    @Test
    void toStringShowsRoles() {
        Author a = seeder.aRandomAuthor(-1L);
        a.addRole(RoleType.ROLE_USER);
        a.addRole(RoleType.ROLE_ADMIN);

        assertThat(a.toString()).as("Author as string").contains("ROLE_ADMIN");
    }

    @Test
    void update() {
        Author author = seeder.authors().get(1);
        author.setFirstName("asdf");

        service.update(author.getId(), author);
        seeder.flush();

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
        seeder.flush();

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteWithComments() {
        Author author = seeder.comments().get(0).getAuthor();
        Long id = author.getId();

        service.deleteById(id);
        seeder.flush();

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void changeNewsAuthor() {
        News n = seeder.news().get(6);
        Author oldAuthor = n.getAuthor();
        Author newAuthor = seeder.authors().get(0);

        newAuthor.addNews(n);

        assertThat(n.getAuthor()).as("News author").isEqualTo(newAuthor);
        assertThat(newAuthor.getNews()).as("New author has the news").contains(n);
        assertThat(oldAuthor.getNews()).as("Old author does not have the news").doesNotContain(n);
    }

    @Test
    void changeCommentAuthor() {
        Comment c = seeder.comments().get(6);
        Author oldAuthor = c.getAuthor();
        Author newAuthor = seeder.authors().get(0);

        newAuthor.addComment(c);

        assertThat(c.getAuthor()).as("Comment author").isEqualTo(newAuthor);
        assertThat(newAuthor.getComments()).as("New author has the comment").contains(c);
        assertThat(oldAuthor.getComments()).as("Old author does not have the comment").doesNotContain(c);
    }
}

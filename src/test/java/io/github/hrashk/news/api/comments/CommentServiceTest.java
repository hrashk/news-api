package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.util.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(CommentService.class)
class CommentServiceTest extends ServiceTest {
    @Autowired
    private CommentService service;

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void findByValidId() {
        Comment comment = seeder.comments().get(0);
        Long validId = comment.getId();

        assertThat(service.findById(validId)).isEqualTo(comment);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void add() {
        Long id = service.add(seeder.aRandomComment(-1L));
        seeder.flush();

        assertThat(id).as("Comment id").isNotNull();
    }

    @Test
    void update() {
        var c = seeder.comments().get(1);
        c.setText("asdf");

        service.updateOrAdd(c.getId(), c);
        seeder.flush();

        assertThat(service.findById(c.getId())).hasFieldOrPropertyWithValue("text", "asdf");
    }

    @Test
    void removeById() {
        Long id = seeder.comments().get(0).getId();

        service.deleteById(id);
        seeder.flush();

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

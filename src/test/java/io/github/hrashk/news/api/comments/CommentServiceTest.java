package io.github.hrashk.news.api.comments;

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
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void add() {
        Long id = service.add(seeder.aRandomComment(-1L));

        assertThat(id).as("Comment id").isNotNull();
    }

    @Test
    void removeById() {
        Long id = seeder.comments().get(0).getId();

        service.deleteById(id);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CommentNotFoundException.class);
    }
}

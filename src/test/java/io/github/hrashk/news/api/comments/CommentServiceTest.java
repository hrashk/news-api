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
    void saveWithNullId() {
        Comment saved = service.addOrReplace(seeder.aRandomComment(-1L));

        assertThat(saved.getId()).as("Comment id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var comment = seeder.aRandomComment(-1L);
        comment.setId(-1L);

        Comment saved = service.addOrReplace(comment);

        assertThat(saved.getId()).as("Comment id").isGreaterThan(0);
    }

    @Test
    void removeById() {
        Long id = seeder.comments().get(0).getId();

        service.delete(seeder.comments().get(0));

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CommentNotFoundException.class);
    }
}

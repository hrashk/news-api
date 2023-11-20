package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({CommentService.class, CommentSamples.class})
class CommentServiceTest {

    @Autowired
    private CommentService service;

    @Autowired
    private CommentRepository repository;
    @Autowired
    private CommentSamples samples;

    private List<Comment> savedComment;

    @BeforeEach
    public void injectComment() {
        this.savedComment = repository.saveAll(samples.twoNewComments());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void findByValidId() {
        Comment comment = savedComment.get(0);
        Long validId = comment.getId();

        assertThat(service.findById(validId)).isEqualTo(comment);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(samples.invalidId()))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void saveWithNullId() {
        Comment saved = service.addOrReplace(samples.withoutId());

        assertThat(saved.getId()).as("Comment id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var comment = samples.withId();
        long originalId = comment.getId(); // the comment object is changed after saving

        Comment saved = service.addOrReplace(comment);

        assertThat(saved.getId()).as("Comment id").isNotEqualTo(originalId);
    }

    @Test
    void removeById() {
        Long id = savedComment.get(0).getId();

        service.delete(savedComment.get(0));

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CommentNotFoundException.class);
    }
}

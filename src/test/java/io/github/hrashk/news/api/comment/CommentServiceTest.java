package io.github.hrashk.news.api.comment;

import io.github.hrashk.news.api.ContainerJpaTest;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentRepository;
import io.github.hrashk.news.api.comments.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import(CommentService.class)
class CommentServiceTest {
    private static final long INVALID_ID = 11333L;

    @Autowired
    private CommentService service;

    @Autowired
    private CommentRepository repository;
    private List<Comment> savedComment;

    @BeforeEach
    public void injectComment() {
        this.savedComment = repository.saveAll(CommentSamples.twoComments());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void findByValidId() {
        Comment news = savedComment.get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(INVALID_ID))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveWithNullId() {
        Comment saved = service.addOrReplace(CommentSamples.withoutId());

        assertThat(saved.getId()).as("Comment id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = CommentSamples.withId();
        long originalid = n.getId(); // the news object is changed after saving

        Comment saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("Comment id").isNotEqualTo(originalid);
    }

    @Test
    void containsValidId() {
        Comment first = savedComment.get(0);

        assertThat(service.contains(first.getId())).isTrue();
    }

    @Test
    void doesNotContainInvalidId() {
        assertThat(service.contains(INVALID_ID)).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedComment.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}

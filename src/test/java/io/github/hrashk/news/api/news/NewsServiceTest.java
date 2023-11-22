package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.util.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(NewsService.class)
class NewsServiceTest extends ServiceTest {
    @Autowired
    private NewsService service;

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 3), null, null)).hasSize(3);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 2), null, null)).hasSize(2);
    }

    @Test
    void findByAuthor() {
        Long authorId = seeder.news().get(0).getAuthor().getId();
        List<News> news = service.findAll(PageRequest.of(0, 10), authorId, null);

        assertThat(news).isNotEmpty();
        assertThat(news).allSatisfy(n -> assertThat(n.getAuthor()).hasFieldOrPropertyWithValue("id", authorId));
    }

    @Test
    void findByCategory() {
        Long categoryId = seeder.news().get(0).getCategory().getId();
        List<News> news = service.findAll(PageRequest.of(0, 10), null, categoryId);

        assertThat(news).isNotEmpty();
        assertThat(news).allSatisfy(n -> assertThat(n.getCategory()).hasFieldOrPropertyWithValue("id", categoryId));
    }

    @Test
    void findByAuthorAndCategory() {
        Long authorId = seeder.news().get(0).getAuthor().getId();
        Long categoryId = seeder.news().get(0).getCategory().getId();
        List<News> news = service.findAll(PageRequest.of(0, 10), authorId, categoryId);

        assertThat(news).isNotEmpty();
        assertThat(news).allSatisfy(n -> assertThat(n.getAuthor()).hasFieldOrPropertyWithValue("id", authorId));
        assertThat(news).allSatisfy(n -> assertThat(n.getCategory()).hasFieldOrPropertyWithValue("id", categoryId));
    }

    @Test
    void findByValidId() {
        News news = seeder.news().get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(NewsNotFoundException.class);
    }

    @Test
    void saveWithNullId() {
        var n = seeder.aRandomNews(-1L);

        News saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("News id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = seeder.aRandomNews(-1L);

        News saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("News id").isGreaterThan(0L);
    }

    @Test
    void deleteWithComments() {
        News news = seeder.news().stream()
                .filter(n -> !n.getComments().isEmpty())
                .findAny().get();
        Long id = news.getId();

        service.delete(news);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NewsNotFoundException.class);
    }
}

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.util.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(NewsService.class)
class NewsServiceTest extends ServiceTest {
    @Autowired
    private NewsService service;

    @Test
    void firstPage() {
        assertThat(service.findAll(new NewsFilter())).hasSize(10);
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
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void add() {
        var n = seeder.aRandomNews(-1L);

        Long id = service.add(n);
        seeder.flush();

        assertThat(id).as("News id").isGreaterThan(0L);
    }

    @Test
    void update() {
        var n = seeder.news().get(1);
        n.setHeadline("asdf");

        service.updateOrAdd(n.getId(), n);
        seeder.flush();

        assertThat(service.findById(n.getId())).hasFieldOrPropertyWithValue("headline", "asdf");
    }

    @Test
    void deleteWithComments() {
        News news = seeder.news().stream()
                .filter(n -> !n.getComments().isEmpty())
                .findAny().get();
        Long id = news.getId();

        service.deleteById(id);
        seeder.flush();

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

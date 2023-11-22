package io.github.hrashk.news.api.news;

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
@Import({NewsService.class, DataSeeder.class})
class NewsServiceTest {
    @Autowired
    private NewsService service;

    @Autowired
    private DataSeeder seeder;

    private List<News> savedNews;

    @BeforeEach
    public void seedNews() {
        seeder.seed(5);
        savedNews = seeder.news();
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
    void findByValidId() {
        News news = savedNews.get(0);
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
        News news = savedNews.stream()
                .filter(n -> !n.getComments().isEmpty())
                .findAny().get();
        Long id = news.getId();

        service.delete(news);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NewsNotFoundException.class);
    }
}

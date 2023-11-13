package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import(NewsService.class)
class NewsServiceTest {
    private static final long INVALID_ID = 11333L;

    @Autowired
    private NewsService service;

    @Autowired
    private NewsRepository repository;
    private List<News> savedNews;

    @BeforeEach
    public void injectNews() {
        this.savedNews = repository.saveAll(NewsSamples.twoNews());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void findByValidId() {
        News news = savedNews.get(0);
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
        News saved = service.addOrReplace(NewsSamples.withoutId());

        assertThat(saved.getId()).as("News id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = NewsSamples.withId();
        long originalid = n.getId(); // the news object is changed after saving

        News saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("News id").isNotEqualTo(originalid);
    }

    @Test
    void containsValidId() {
        News first = savedNews.get(0);

        assertThat(service.contains(first.getId())).isTrue();
    }

    @Test
    void doesNotContainInvalidId() {
        assertThat(service.contains(INVALID_ID)).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedNews.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}

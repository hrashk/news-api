package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({NewsService.class, NewsSamples.class})
class NewsServiceTest {
    @Autowired
    private NewsService service;

    @Autowired
    private NewsRepository repository;
    @Autowired
    private NewsSamples samples;
    private List<News> savedNews;

    @BeforeEach
    public void injectNews() {
        this.savedNews = repository.saveAll(samples.twoNewsWithNewDependencies());
    }

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 1))).hasSize(1);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 1))).hasSize(1);
    }

    @Test
    void findByValidId() {
        News news = savedNews.get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(samples.invalidId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveWithNullId() {
        News saved = service.addOrReplace(samples.withoutId());

        assertThat(saved.getId()).as("News id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = samples.withInvalidId();
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
        assertThat(service.contains(samples.invalidId())).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedNews.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}

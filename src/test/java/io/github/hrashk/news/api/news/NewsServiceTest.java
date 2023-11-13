package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerJpaTest
@Import(NewsService.class)
class NewsServiceTest {
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
}

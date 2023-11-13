package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.AuthorsRepository;
import io.github.hrashk.news.api.news.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ContainerJpaTest
class SampleDataInjectorTest {
    @Autowired
    private AuthorsRepository authorsRepo;
    @Autowired
    private NewsRepository newsRepo;

    @Test
    void sampleDataIsLoaded() {
        new SampleDataInjector(authorsRepo, newsRepo).run();
        assertAll(
                () -> assertThat(authorsRepo.count()).as("Authors count").isGreaterThan(0L),
                () -> assertThat(newsRepo.count()).as("News count").isGreaterThan(0L)
        );
    }
}

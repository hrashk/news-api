package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.AuthorsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ContainerJpaTest
class SampleDataInjectorTest {
    @Autowired
    private AuthorsRepository repository;

    @Test
    void sampleDataIsLoaded() {
        new SampleDataInjector(repository).run();
        assertThat(repository.count()).isGreaterThan(0L);
    }
}

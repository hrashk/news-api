package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.news.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ContainerJpaTest
@Import(SampleDataInjector.class) // will run automatically
class SampleDataInjectorTest {
    @Autowired
    private AuthorRepository authorsRepo;
    @Autowired
    private NewsRepository newsRepo;
    @Autowired
    private CategoryRepository categoryRepo;

    @Test
    void sampleDataIsLoaded() {
        assertAll(
                () -> assertThat(authorsRepo.count()).as("Authors count").isGreaterThan(0L),
                () -> assertThat(newsRepo.count()).as("News count").isGreaterThan(0L),
                () -> assertThat(categoryRepo.count()).as("Categories count").isGreaterThan(0L)
        );
    }
}

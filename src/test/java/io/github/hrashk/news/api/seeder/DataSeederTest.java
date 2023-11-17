package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.ContainerJpaTest;
import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.news.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ContainerJpaTest
@Import(DataSeeder.class)
class DataSeederTest {
    @Autowired
    private AuthorRepository authorsRepo;
    @Autowired
    private NewsRepository newsRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private DataSeeder seeder;

    @Test
    void sampleDataIsLoaded() {
        seeder.seed(10);

        assertAll(
                () -> assertThat(authorsRepo.count()).as("Authors count").isGreaterThan(5L),
                () -> assertThat(newsRepo.count()).as("News count").isGreaterThan(5L),
                () -> assertThat(categoryRepo.count()).as("Categories count").isGreaterThan(5L)
        );
    }
}

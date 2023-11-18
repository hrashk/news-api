package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ContainerJpaTest
@Import(DataSeeder.class)
class DataSeederTest {
    @Autowired
    private DataSeeder seeder;

    @Test
    void sampleDataIsLoaded() {
        seeder.seed(10);

        assertAll(
                () -> assertThat(seeder.authorsCount()).as("Authors count").isGreaterThan(5L),
                () -> assertThat(seeder.newsCount()).as("News count").isGreaterThan(5L),
                () -> assertThat(seeder.categoriesCount()).as("Categories count").isGreaterThan(5L)
        );
    }
}

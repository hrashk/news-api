package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.util.ContainerJpaTest;
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
        int size = 10;
        seeder.seed(size);
        seeder.flush();

        assertAll(
                () -> assertThat(seeder.authors()).as("Authors").hasSize(size),
                () -> assertThat(seeder.authors()).as("Author ids").noneMatch(a -> a.getId() == null),
                () -> assertThat(seeder.news()).as("News").hasSize(size),
                () -> assertThat(seeder.news()).as("News ids").noneMatch(a -> a.getId() == null),
                () -> assertThat(seeder.categories()).as("Categories").hasSize(size),
                () -> assertThat(seeder.categories()).as("Categor ids").noneMatch(a -> a.getId() == null),
                () -> assertThat(seeder.comments()).as("Comments").hasSize(size),
                () -> assertThat(seeder.comments()).as("Comment ids").noneMatch(a -> a.getId() == null)
        );
    }
}

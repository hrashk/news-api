package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SampleDataSeeder implements CommandLineRunner {
    private final AuthorRepository authorsRepo;
    private final NewsRepository newsRepo;
    private final CategoryRepository categoryRepo;
    private final SampleDataGenerator generator = new SampleDataGenerator();

    @Override
    public void run(String... args) {
        authorsRepo.saveAll(generator.sampleAuthors(10));
        categoryRepo.saveAll(generator.sampleCategories(10));
        newsRepo.saveAll(generator.sampleNews(10));
    }
}

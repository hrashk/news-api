package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.AuthorsRepository;
import io.github.hrashk.news.api.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SampleDataInjector implements CommandLineRunner {
    private final AuthorsRepository authorsRepo;
    private final NewsRepository newsRepo;
    private final SampleDataGenerator generator = new SampleDataGenerator();

    @Override
    public void run(String... args) {
        authorsRepo.saveAll(generator.sampleAuthors(10));
        newsRepo.saveAll(generator.sampleNews(10));
    }
}

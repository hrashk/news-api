package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class SampleDataInjector implements CommandLineRunner {
    private final AuthorRepository repository;
    private final SampleDataGenerator generator = new SampleDataGenerator();

    @Override
    public void run(String... args) {
        repository.saveAll(generator.sampleAuthors(10));
    }
}

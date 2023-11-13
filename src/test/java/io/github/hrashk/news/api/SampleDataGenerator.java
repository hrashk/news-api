package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.news.News;
import net.datafaker.Faker;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class SampleDataGenerator {
    private final Random random = ThreadLocalRandom.current();
    private final Faker faker = new Faker(random);

    public List<Author> sampleAuthors(int count) {
        return generateSample(count, this::aRandomAuthor);
    }

    public Iterable<News> sampleNews(int count) {
        return generateSample(count, this::aRandomNews);
    }

    private <T> List<T> generateSample(int count, IntFunction<T> authorIntFunction) {
        return IntStream.range(1, count + 1)
                .mapToObj(authorIntFunction)
                .toList();
    }

    private Author aRandomAuthor(long id) {
        return Author.builder()
                .id(id)
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    private News aRandomNews(long id) {
        return News.builder()
                .id(id)
                .headline(faker.lorem().sentence())
                .content(faker.lorem().paragraph())
                .build();
    }
}

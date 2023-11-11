package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.Author;
import net.datafaker.Faker;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class SampleDataGenerator {
    private final Random random = ThreadLocalRandom.current();
    private final Faker faker = new Faker(random);

    public List<Author> sampleAuthors(int count) {
        return IntStream.range(0, count)
                .mapToObj(idx -> aRandomAuthort(idx + 1))
                .toList();
    }

    private Author aRandomAuthort(long idx) {
        return Author.builder()
                .id(idx)
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }
}

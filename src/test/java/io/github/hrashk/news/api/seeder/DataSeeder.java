package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongFunction;
import java.util.stream.LongStream;

@TestComponent
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class DataSeeder {
    private final AuthorRepository authorsRepo;
    private final NewsRepository newsRepo;
    private final CategoryRepository categoryRepo;
    private final Random random = ThreadLocalRandom.current();
    private final Faker faker = new Faker(random);

    private List<Author> authors;
    private List<Category> categories;
    private List<News> news;

    public void seed(int count) {
        authors = authorsRepo.saveAll(sampleAuthors(count));
        categories = categoryRepo.saveAll(sampleCategories(count));
        news = newsRepo.saveAll(sampleNews(count));
    }

    public List<Author> sampleAuthors(int count) {
        return generateSample(count, this::aRandomAuthor);
    }

    public Iterable<Category> sampleCategories(int count) {
        return generateSample(count, this::aRandomCategory);
    }

    public Iterable<News> sampleNews(int count) {
        return generateSample(count, this::aRandomNews);
    }

    private <T> List<T> generateSample(int count, LongFunction<T> entityGenerator) {
        return LongStream.range(1, count + 1)
                .mapToObj(entityGenerator)
                .toList();
    }

    private Author aRandomAuthor(long id) {
        return Author.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    private Category aRandomCategory(long id) {
        return Category.builder()
                .name(faker.book().genre())
                .build();
    }

    private News aRandomNews(long id) {
        return News.builder()
                .author(randomItem(authors))
                .category(randomItem(categories))
                .headline(faker.lorem().sentence())
                .content(faker.lorem().paragraph())
                .build();
    }

    private <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }
}

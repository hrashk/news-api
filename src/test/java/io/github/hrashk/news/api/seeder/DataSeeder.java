package io.github.hrashk.news.api.seeder;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongFunction;
import java.util.stream.LongStream;

@TestComponent
@RequiredArgsConstructor
public class DataSeeder {
    private final AuthorRepository authorsRepo;
    private final NewsRepository newsRepo;
    private final CategoryRepository categoryRepo;
    private final Random random = ThreadLocalRandom.current();
    private final Faker faker = new Faker(random);

    public void seed(int count) {
        List<Author> authors = authorsRepo.saveAll(sampleAuthors(count));
        List<Category> categories = categoryRepo.saveAll(sampleCategories(count));
        newsRepo.saveAll(sampleNews(count, authors, categories));
    }

    long authorsCount() {
        return authorsRepo.count();
    }

    long categoriesCount() {
        return categoryRepo.count();
    }

    long newsCount() {
        return newsRepo.count();
    }

    public List<Author> sampleAuthors(int count) {
        return generateSample(count, this::aRandomAuthor);
    }

    public Iterable<News> sampleNews(int count, List<Author> authors, List<Category> categories) {
        return generateSample(count, id -> aRandomNews(id, authors, categories));
    }

    public Iterable<Category> sampleCategories(int count) {
        return generateSample(count, this::aRandomCategory);
    }

    private <T> List<T> generateSample(int count, LongFunction<T> entityGenerator) {
        return LongStream.range(1, count + 1)
                .mapToObj(entityGenerator)
                .toList();
    }

    private Author aRandomAuthor(long id) {
        return Author.builder()
                .id(id)
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();
    }

    private News aRandomNews(long id, List<Author> authors, List<Category> categories) {
        return News.builder()
                .id(id)
                .author(randomItem(authors))
                .category(randomItem(categories))
                .headline(faker.lorem().sentence())
                .content(faker.lorem().paragraph())
                .build();
    }

    private <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    private Category aRandomCategory(long id) {
        return Category.builder()
                .id(id)
                .name(faker.book().genre())
                .build();
    }
}

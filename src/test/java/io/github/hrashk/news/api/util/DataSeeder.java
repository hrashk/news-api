package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.authors.AuthorService;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentRepository;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsRepository;
import io.github.hrashk.news.api.security.RoleType;
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
    private final AuthorService authorService;
    private final NewsRepository newsRepo;
    private final CategoryRepository categoryRepo;
    private final CommentRepository commentRepository;

    private final Random random = ThreadLocalRandom.current();
    private final Faker faker = new Faker(random);

    private List<Author> authors;
    private List<Category> categories;
    private List<News> news;
    private List<Comment> comments;

    public void seed(int count) {
        authors = sampleAuthors(count);
        authors.get(0).addRole(RoleType.ROLE_ADMIN);
        authors.get(1).addRole(RoleType.ROLE_MODERATOR);
        authors.get(2).addRole(RoleType.ROLE_USER);
        authors.get(3).addRole(RoleType.ROLE_MODERATOR);
        authors.get(3).addRole(RoleType.ROLE_USER);

        authors = authors.stream().map(this::saveAndReturnDecoded).toList();
        categories = categoryRepo.saveAll(sampleCategories(count));
        news = newsRepo.saveAll(sampleNews(count));
        comments = commentRepository.saveAll(sampleComments(count));
    }

    public void flush() {
        authorsRepo.flush();
        categoryRepo.flush();
        newsRepo.flush();
        commentRepository.flush();
    }

    private Author saveAndReturnDecoded(Author a) {
        String decoded = a.getPassword();

        authorService.encodePassword(a);
        a = authorsRepo.save(a);
        a.setPassword(decoded);

        return a;
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

    private Iterable<Comment> sampleComments(int count) {
        return generateSample(count, this::aRandomComment);
    }

    private <T> List<T> generateSample(int count, LongFunction<T> entityGenerator) {
        return LongStream.range(1, count + 1)
                .mapToObj(entityGenerator)
                .toList();
    }

    public Author aRandomAuthor(long id) {
        return new Author().toBuilder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.internet().username())
                .password(faker.internet().password())
                .build();
    }

    public Category aRandomCategory(long id) {
        return Category.builder()
                .name(faker.book().genre())
                .build();
    }

    public News aRandomNews(long ignored) {
        Author author = randomItem(authors);

        News newsItem = new News().toBuilder()
                .category(randomItem(categories))
                .headline(faker.lorem().sentence())
                .content(faker.lorem().paragraph(10))
                .build();
        author.addNews(newsItem);

        return newsItem;
    }

    public Comment aRandomComment(long ignored) {
        Author author = randomItem(authors);
        News newsItem = randomItem(news);

        Comment comment = Comment.builder()
                .text(faker.lorem().paragraph(10))
                .build();
        author.addComment(comment);
        newsItem.addComment(comment);

        return comment;
    }

    private <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }
}

package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorRepository;
import io.github.hrashk.news.api.authors.AuthorService;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryRepository;
import io.github.hrashk.news.api.categories.web.UpsertCategoryRequest;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentRepository;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsRepository;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.security.RoleType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
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

    /**
     * 0 - has no roles; 1 - admin; 2 - moderator+user; the rest are just users
     */
    private List<Author> authors;
    private List<Credentials> creds;

    /**
     * 0, 1, 2 - have no news
     */
    private List<Category> categories;

    private List<News> news;
    private List<Comment> comments;

    public void seed(int count) {
        authors = sampleAuthors(count);
        authors.get(1).addRole(RoleType.ROLE_ADMIN);
        authors.get(2).addRole(RoleType.ROLE_MODERATOR);
        addUserRole(authors);

        creds = authors.stream().map(Credentials::new).collect(Collectors.toCollection(ArrayList::new));
        authors.forEach(authorService::encodePassword);

        authors = authorsRepo.saveAll(authors);
        categories = categoryRepo.saveAll(sampleCategories(count));

        news = sampleNews(count);
        authors.get(1).addNews(news.get(0)); // admin
        authors.get(2).addNews(news.get(1)); // moderator
        authors.get(3).addNews(news.get(2)); // user
        news = newsRepo.saveAll(news);

        comments = sampleComments(count);
        authors.get(1).addComment(comments.get(0)); // admin
        authors.get(2).addComment(comments.get(1)); // moderator
        authors.get(3).addComment(comments.get(2)); // user
        comments = commentRepository.saveAll(comments);
    }

    private void addUserRole(List<Author> authors) {
        authors.subList(2, authors.size()).forEach(a -> a.addRole(RoleType.ROLE_USER));
    }

    public void flush() {
        authorsRepo.flush();
        categoryRepo.flush();
        newsRepo.flush();
        commentRepository.flush();
    }

    public Credentials admin() {
        return creds.get(1);
    }

    public Long adminId() {
        return authors.get(1).getId();
    }

    public Credentials moderator() {
        return creds.get(2);
    }

    public Long moderatorId() {
        return authors.get(2).getId();
    }

    public Credentials plainUser() {
        return creds.get(3);
    }

    public Long plainUserId() {
        return authors.get(3).getId();
    }

    public Long authorId(int index) {
        return authors.get(index).getId();
    }

    public Credentials withoutRoles() {
        return creds.get(0);
    }

    public Long withoutRolesId() {
        return authors.get(0).getId();
    }

    public Credentials fakeUser() {
        return new Credentials("fake", "author");
    }

    public Long categoryId(int index) {
        return categories.get(index).getId();
    }

    public Long newsId(int index) {
        return news.get(index).getId();
    }

    public List<Author> sampleAuthors(int count) {
        return generateSample(count, this::aRandomAuthor);
    }

    public Iterable<Category> sampleCategories(int count) {
        return generateSample(count, this::aRandomCategory);
    }

    public List<News> sampleNews(int count) {
        return generateSample(count, this::aRandomNews);
    }

    private List<Comment> sampleComments(int count) {
        return generateSample(count, this::aRandomComment);
    }

    private <T> List<T> generateSample(int count, LongFunction<T> entityGenerator) {
        return LongStream.range(1, count + 1)
                .mapToObj(entityGenerator)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Author aRandomAuthor(long id) {
        return new Author().toBuilder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.internet().username())
                .password(faker.internet().password())
                .build();
    }

    public UpsertAuthorRequest randomAuthorRequest() {
        return new UpsertAuthorRequest(
                faker().name().firstName(),
                faker().name().lastName(),
                faker().internet().username(),
                faker().internet().password());
    }

    public Category aRandomCategory(long id) {
        return Category.builder()
                .name(faker.book().genre())
                .build();
    }

    public UpsertCategoryRequest randomCategoryRequest() {
        return new UpsertCategoryRequest(faker.book().genre());
    }

    public News aRandomNews(long ignored) {
        Author author = randomItem(authors, 4);

        News newsItem = new News().toBuilder()
                .category(randomItem(categories, 3))
                .headline(faker.lorem().sentence())
                .content(faker.lorem().paragraph(10))
                .build();
        author.addNews(newsItem);

        return newsItem;
    }

    public UpsertNewsRequest randomNewsRequest(Long authorId) {
        return new UpsertNewsRequest(
                authorId,
                randomItem(categories, 3).getId(),
                faker.lorem().sentence(),
                faker.lorem().paragraph(10));
    }

    public Comment aRandomComment(long ignored) {
        Author author = randomItem(authors, 4);
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

    /**
     * skip some items at the beginning
     */
    private <T> T randomItem(List<T> items, int from) {
        return items.get(from + random.nextInt(items.size() - from));
    }

    public News aNewsNotByAuthor(Long authorId) {
        return news.stream()
                .filter(n -> !Objects.equals(n.getAuthor().getId(), authorId))
                .findAny().get();
    }

    public Comment aCommentNotByAuthor(Long authorId) {
        return comments.stream()
                .filter(c -> !Objects.equals(c.getAuthor().getId(), authorId))
                .findAny().get();
    }

    public Credentials newsAuthorCreds(News news) {
        String username = news.getAuthor().getUsername();

        return new Credentials(username, unencodedPassword(username));
    }

    public String unencodedPassword(String username) {
        return creds.stream()
                .filter(c -> Objects.equals(c.username(), username))
                .map(Credentials::password)
                .findAny().get();
    }
}

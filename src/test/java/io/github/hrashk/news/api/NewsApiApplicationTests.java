package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.categories.web.CategoryListResponse;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.comments.web.CommentResponse;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.NewsWithCountResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.util.DataSeeder;
import io.github.hrashk.news.api.util.PostgreSQLInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@Import(DataSeeder.class)
class NewsApiApplicationTests {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private DataSeeder seeder;

    @BeforeEach
    void injectSampleData() {
        seeder.seed(10);
    }

    @Test
    void deleteNewsWithComments() {
        NewsWithCountResponse news = findNewsWithComments();
        assertThat(news.id()).isNotNull();

        ResponseEntity<Void> response = rest.exchange("/api/v1/news/{id}?userId={userId}",
                HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, news.id(), news.authorId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> errorResponse = rest.getForEntity("/api/v1/news/{id}", ErrorInfo.class, news.id());
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @CsvSource({"/api/v1/news/{id}", "/api/v1/news/{id}?userId=111222333"})
    void deleteNewsWithInvalidUser(String url) {
        Long id = findNewsWithComments().id();
        assertThat(id).isNotNull();

        ResponseEntity<Void> response = rest.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteWithInvalidId() {
        ResponseEntity<Void> response = rest.exchange("/api/v1/news/{id}?userId=12358", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, "gogi");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private NewsWithCountResponse findNewsWithComments() {
        return fetchNews().stream()
                .filter(n -> n.commentsCount() > 0)
                .findFirst().orElseThrow();
    }

    @Test
    void deleteAuthorWithNews() {
        deleteAuthor(findAuthorWithComments());
    }

    private Long findAuthorWithComments() {
        Long newsId = findNewsWithComments().id();

        List<CommentResponse> comments = fetchComments(newsId);

        return comments.get(0).authorId();
    }

    private List<CommentResponse> fetchComments(Long newsId) {
        ResponseEntity<NewsResponse> response = rest.getForEntity("/api/v1/news/{id}", NewsResponse.class, newsId);
        List<CommentResponse> comments = Objects.requireNonNull(response.getBody()).comments();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(comments).isNotEmpty()
        );

        return comments;
    }

    @Test
    void deleteAuthorWithComments() {
        deleteAuthor(seeder.comments().get(2).getAuthor().getId());
    }

    @Test
    void deletingCategoryWithNewsFails() {
        var news = fetchNews().get(4);

        ResponseEntity<ErrorInfo> response = rest.exchange("/api/v1/categories/{id}",
                HttpMethod.DELETE, HttpEntity.EMPTY, ErrorInfo.class, news.categoryId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addNews() {
        var authors = fetchAuthors();
        var categories = fetchCategories();

        UpsertNewsRequest request = new UpsertNewsRequest(authors.get(3).id(), categories.get(4).id(), "", "");
        ResponseEntity<NewsResponse> response = rest.postForEntity("/api/v1/news", request, NewsResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrPropertiesExcept("comments")
        );
    }

    @Test
    void findNewsByAuthorAndCategory() {
        News firstNews = seeder.news().get(0);
        Long authorId = firstNews.getAuthor().getId();
        Long categoryId = firstNews.getCategory().getId();

        ResponseEntity<NewsListResponse> entity = rest.getForEntity(
                "/api/v1/news?authorId={aid}&categoryId={cid}", NewsListResponse.class,
                authorId,
                categoryId);

        List<NewsWithCountResponse> news = Objects.requireNonNull(entity.getBody()).news();
        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(news).isNotEmpty(),
                () -> assertThat(news).allSatisfy(n -> assertThat(n).hasFieldOrPropertyWithValue("authorId", authorId)),
                () -> assertThat(news).allSatisfy(n -> assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
        );
    }

    @ParameterizedTest
    @CsvSource({"/api/v1/news/{id}", "/api/v1/news/{id}?userId=111222333"})
    void updateNewsWithInvalidUser(String url) {
        var news = fetchNews().get(1);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UpsertNewsRequest request = new UpsertNewsRequest(news.authorId(), news.categoryId(), "", "");
        ResponseEntity<NewsResponse> response = rest.exchange(url, HttpMethod.PUT, new HttpEntity<>(request, headers), NewsResponse.class, news.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private List<AuthorResponse> fetchAuthors() {
        ResponseEntity<AuthorListResponse> response = rest.getForEntity("/api/v1/authors", AuthorListResponse.class);

        List<AuthorResponse> authors = Objects.requireNonNull(response.getBody()).authors();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(authors).hasSizeGreaterThan(5)
        );

        return authors;
    }

    private List<CategoryResponse> fetchCategories() {
        ResponseEntity<CategoryListResponse> response = rest.getForEntity("/api/v1/categories", CategoryListResponse.class);

        List<CategoryResponse> categories = Objects.requireNonNull(response.getBody()).categories();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(categories).hasSizeGreaterThan(5)
        );

        return categories;
    }

    private List<NewsWithCountResponse> fetchNews() {
        ResponseEntity<NewsListResponse> entity = rest.getForEntity("/api/v1/news", NewsListResponse.class);

        List<NewsWithCountResponse> news = Objects.requireNonNull(entity.getBody()).news();
        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(news).hasSizeGreaterThan(5)
        );

        assertThat(news).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties());

        return news;
    }

    private void deleteEntity(String name, Long id) {
        assertThat(id).isNotNull();

        ResponseEntity<Void> response = rest.exchange("/api/v1/{name}/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, name, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> errorResponse = rest.getForEntity("/api/v1/{name}/{id}", ErrorInfo.class, name, id);
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void deleteAuthor(Long id) {
        deleteEntity("authors", id);
    }

    private void deleteCategory(Long id) {
        deleteEntity("categories", id);
    }
}

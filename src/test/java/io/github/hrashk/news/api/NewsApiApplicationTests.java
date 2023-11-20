package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.categories.web.CategoryListResponse;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.NewsWithCountResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.seeder.DataSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        deleteNews(seeder.comments().get(3).getNews().getId());
    }

    @Test
    void deleteAuthorWithNews() {
        var news = fetchNews().get(3);

        deleteAuthor(news.authorId());
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
        ResponseEntity<Void> response = rest.exchange("/api/v1/{name}/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, name, id);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> errorResponse = rest.getForEntity("/api/v1/{name}/{id}", ErrorInfo.class, name, id);
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void deleteNews(Long id) {
        deleteEntity("news", id);
    }

    private void deleteAuthor(Long id) {
        deleteEntity("authors", id);
    }

    private void deleteCategory(Long id) {
        deleteEntity("categories", id);
    }
}

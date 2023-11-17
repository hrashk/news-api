package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.categories.web.CategoryListResponse;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.seeder.DataSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
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

    List<AuthorResponse> fetchAuthors() {
        ResponseEntity<AuthorListResponse> response = rest.getForEntity("/api/v1/authors", AuthorListResponse.class);

        List<AuthorResponse> authors = Objects.requireNonNull(response.getBody()).authors();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(authors).hasSizeGreaterThan(5)
        );

        return authors;
    }

    List<CategoryResponse> fetchCategories() {
        ResponseEntity<CategoryListResponse> response = rest.getForEntity("/api/v1/categories", CategoryListResponse.class);

        List<CategoryResponse> categories = Objects.requireNonNull(response.getBody()).categories();

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(categories).hasSizeGreaterThan(5)
        );

        return categories;
    }

    @Test
    void fetchNews() {
        ResponseEntity<NewsListResponse> entity = rest.getForEntity("/api/v1/news", NewsListResponse.class);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).hasSizeGreaterThan(5)
        );

        assertThat(entity.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties());
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
}

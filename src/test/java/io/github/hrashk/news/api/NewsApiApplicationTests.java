package io.github.hrashk.news.api;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.categories.web.CategoryListResponse;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.seeder.SampleDataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@Import(SampleDataSeeder.class)
class NewsApiApplicationTests {
    @Autowired
    private TestRestTemplate rest;

    @Test
    void fetchAuthors() {
        ResponseEntity<AuthorListResponse> entity = rest.getForEntity("/api/v1/authors", AuthorListResponse.class);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().authors()).hasSizeGreaterThan(5)
        );
    }

    @Test
    void fetchCategories() {
        ResponseEntity<CategoryListResponse> response = rest.getForEntity("/api/v1/categories", CategoryListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().categories()).hasSizeGreaterThan(5)
        );
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
        UpsertNewsRequest request = new UpsertNewsRequest(9L, 10L, "", "");
        ResponseEntity<NewsResponse> response = rest.postForEntity("/api/v1/news", request, NewsResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrPropertiesExcept("comments")
        );
    }
}

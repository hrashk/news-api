package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@Import(DataSeeder.class)
public abstract class ControllerTest {
    protected static final Long INVALID_ID = 111222333L;
    @Autowired
    protected TestRestTemplate rest;
    @Autowired
    protected DataSeeder seeder;

    @BeforeEach
    void injectSampleData() {
        seeder.seed(10);
    }

    public <T> ResponseEntity<T> put(String url, Object request, Author a, Class<T> responseType, Object... urlVariables) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return rest.withBasicAuth(a.getUsername(), a.getPassword())
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(request, headers), responseType, urlVariables);
    }

    public <T> ResponseEntity<T> delete(String url, Author a, Class<T> responseType, Object... urlVariables) {
        return rest.withBasicAuth(a.getUsername(), a.getPassword())
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, responseType, urlVariables);
    }

    public ResponseEntity<Void> delete(String url, Author a, Object... urlVariables) {
        return delete(url, a, Void.class, urlVariables);
    }

    static Stream<Arguments> users() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::moderator, "moderator"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::plainUser, "user"));
    }

    static Stream<Arguments> adminAndModerator() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::moderator, "moderator"));
    }
}

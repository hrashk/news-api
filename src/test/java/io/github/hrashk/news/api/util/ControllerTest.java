package io.github.hrashk.news.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;

import java.util.function.Function;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"logging.level.org.apache.hc.client5.http=DEBUG"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@Import(DataSeeder.class)
public abstract class ControllerTest {
    protected static final Long INVALID_ID = 111222333L;
    @Autowired
    protected TestRestTemplate rest;
    @Autowired
    protected DataSeeder seeder;

    protected final Credentials authorNotInSystem = new Credentials("fake", "author");

    @BeforeEach
    void injectSampleData() {
        seeder.seed(10);
        rest.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public <T> ResponseEntity<T> put(String url, Object request, Credentials a, Class<T> responseType, Object... urlVariables) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return rest.withBasicAuth(a.username(), a.password())
                .exchange(url, HttpMethod.PUT, new HttpEntity<>(request, headers), responseType, urlVariables);
    }

    public <T> ResponseEntity<T> delete(String url, Credentials a, Class<T> responseType, Object... urlVariables) {
        return rest.withBasicAuth(a.username(), a.password())
                .exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, responseType, urlVariables);
    }

    public ResponseEntity<Void> delete(String url, Credentials a, Object... urlVariables) {
        return delete(url, a, Void.class, urlVariables);
    }

    static Stream<Arguments> users() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::moderator, "moderator"),
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::plainUser, "user"));
    }

    static Stream<Arguments> adminAndModerator() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::moderator, "moderator"));
    }
}

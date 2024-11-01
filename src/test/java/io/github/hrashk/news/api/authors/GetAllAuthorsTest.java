package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetAllAuthorsTest extends ControllerTest {
    @Test
    void firstPage() {
        Author admin = seeder.admin();

        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull()
        );

        assertAll(
                () -> assertThat(response.getBody().authors()).hasSize(10),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        Author admin = seeder.admin();

        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_URL + "?page=1&size=3", AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull()
        );

        assertAll(
                () -> assertThat(response.getBody().authors()).hasSize(3),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void unauthorizedWhenNoCreds() {
        ResponseEntity<AuthorListResponse> response = rest
                .getForEntity(Constants.AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(response.getBody()).isNull()
        );
    }

    @Test
    void unauthorizedWhenWrongCreds() {
        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth("fake", "password")
                .getForEntity(Constants.AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(response.getBody()).isNull()
        );
    }

    @ParameterizedTest(name="{1}")
    @MethodSource("forbiddenUsers")
    void forbiddenIfNotAdmin(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);

        ResponseEntity<Map> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(Constants.AUTHORS_URL, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    static Stream<Arguments> forbiddenUsers() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::withoutRoles, "no role"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::moderator, "moderator"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::plainUser, "user"));
    }
}

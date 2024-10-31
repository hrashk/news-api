package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthorControllerTest extends ControllerTest {
    @Test
    void add() {
        Author a = seeder.admin();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .postForEntity(Constants.AUTHORS_URL, request, AuthorResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().firstName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody().lastName()).isEqualTo("ipsum")
        );
    }

    @Test
    void addBroken() {
        Author m = seeder.moderator();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "  ", null, "random", "password");

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(m.getUsername(), m.getPassword())
                .postForEntity(Constants.AUTHORS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("firstName", "lastName")
        );
    }

    @ParameterizedTest(name="{1}")
    @MethodSource("users")
    void update(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = seeder.plainUser();
        var request = new UpsertAuthorRequest(
                a.getFirstName(), "lorem", a.getUsername(), "password");

        ResponseEntity<AuthorResponse> response =
                put(Constants.AUTHORS_ID_URL, request, userProvider.apply(seeder), AuthorResponse.class, a.getId());

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().lastName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    static Stream<Arguments> users() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::moderator, "moderator"),
                Arguments.of((Function<DataSeeder, Author>) DataSeeder::plainUser, "plainUser"));
    }

    @Test
    void updateMissing() {
        Long authorId = INVALID_ID;
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        ResponseEntity<AuthorResponse> response = put(Constants.AUTHORS_ID_URL, request, seeder.moderator(), AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().firstName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody().lastName()).isEqualTo("ipsum")
        );
    }

    @Test
    void deleteWithNews() {
        Long authorId = seeder.news().get(0).getAuthor().getId();

        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteWithAuthors() {
        Long authorId = seeder.comments().get(0).getAuthor().getId();

        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteMissing() {
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }
}

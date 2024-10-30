package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetAllAuthorsTest extends ControllerTest {

    private static final String AUTHORS_URL = "/api/v1/authors";

    @Test
    void firstPage() {
        Author admin = seeder.admin();

        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(AUTHORS_URL, AuthorListResponse.class);

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
                .getForEntity(AUTHORS_URL + "?page=1&size=3", AuthorListResponse.class);

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
                .getForEntity(AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(response.getBody()).isNull()
        );
    }

    @Test
    void unauthorizedWhenWrongCreds() {
        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth("fake", "password")
                .getForEntity(AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(response.getBody()).isNull()
        );
    }

    @Test
    void forbiddenWhenNoRole() {
        Author author = seeder.withoutRoles();

        ResponseEntity<Map> response = rest.withBasicAuth(author.getUsername(), author.getPassword())
                .getForEntity(AUTHORS_URL, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void forbiddenWhenModerator() {
        Author author = seeder.moderator();

        ResponseEntity<Map> response = rest.withBasicAuth(author.getUsername(), author.getPassword())
                .getForEntity(AUTHORS_URL, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void forbiddenWhenPlainUser() {
        Author author = seeder.plainUser();

        ResponseEntity<Map> response = rest.withBasicAuth(author.getUsername(), author.getPassword())
                .getForEntity(AUTHORS_URL, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}

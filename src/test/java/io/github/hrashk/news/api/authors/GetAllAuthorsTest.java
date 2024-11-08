package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}

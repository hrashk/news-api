package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetAuthorByIdTest extends ControllerTest {
    @Test
    void findById() {
        Author a = seeder.moderator();
        Long authorId = seeder.plainUser().getId();

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(Constants.AUTHORS_ID_URL, AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(authorId)
        );
    }

    @Test
    void findMissing() {
        Author admin = seeder.admin();
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }
}

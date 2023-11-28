package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthorControllerTest extends ControllerTest {

    private static final String AUTHORS_URL = "/api/v1/authors";
    private static final String AUTHORS_ID_URL = AUTHORS_URL + "/{id}";

    @Test
    void firstPage() {
        ResponseEntity<AuthorListResponse> response = rest.getForEntity(AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().authors()).hasSize(10),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        ResponseEntity<AuthorListResponse> response = rest.getForEntity(AUTHORS_URL + "?page=1&size=3", AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().authors()).hasSize(3),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void findById() {
        Long authorId = seeder.authors().get(0).getId();

        ResponseEntity<AuthorResponse> response = rest.getForEntity(AUTHORS_ID_URL, AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(authorId)
        );
    }

    @Test
    void findMissing() {
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.getForEntity(AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @Test
    void add() {
        UpsertAuthorRequest request = new UpsertAuthorRequest("lorem", "ipsum");

        ResponseEntity<AuthorResponse> response = rest.postForEntity(AUTHORS_URL, request, AuthorResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().firstName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody().lastName()).isEqualTo("ipsum")
        );
    }

    @Test
    void addBroken() {
        UpsertAuthorRequest request = new UpsertAuthorRequest("  ", null);

        ResponseEntity<ErrorInfo> response = rest.postForEntity(AUTHORS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("firstName", "lastName")
        );
    }

    @Test
    void update() {
        Author author = seeder.authors().get(0);
        Long authorId = author.getId();
        var request = new UpsertAuthorRequest(author.getFirstName(), "lorem");

        ResponseEntity<AuthorResponse> response = put(AUTHORS_ID_URL, request, AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().lastName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void updateMissing() {
        Long authorId = INVALID_ID;
        UpsertAuthorRequest request = new UpsertAuthorRequest("lorem", "ipsum");

        ResponseEntity<AuthorResponse> response = put(AUTHORS_ID_URL, request, AuthorResponse.class, authorId);

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

        ResponseEntity<Void> response = delete(AUTHORS_ID_URL, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteWithAuthors() {
        Long authorId = seeder.comments().get(0).getAuthor().getId();

        ResponseEntity<Void> response = delete(AUTHORS_ID_URL, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteMissing() {
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }
}

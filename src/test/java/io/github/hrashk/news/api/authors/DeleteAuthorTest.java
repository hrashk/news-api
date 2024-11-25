package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import io.github.hrashk.news.api.util.HttpExecutable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DeleteAuthorTest extends ControllerTest {
    @Test
    void deleteWithNews() {
        Long authorId = seeder.aNewsNotByAuthor(seeder.adminId()).getAuthor().getId();

        Credentials a = seeder.admin();
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, a, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteSelf() {
        Long authorId = seeder.plainUserId();

        Credentials a = seeder.plainUser();
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, a, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deleteWithComments() {
        Long authorId = seeder.aCommentNotByAuthor(seeder.moderatorId()).getAuthor().getId();

        Credentials m = seeder.moderator();
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, m, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(m.username(), m.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteMissing() {
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(Constants.AUTHORS_ID_URL, seeder.admin(), ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long aUserId = seeder.authors().get(6).getId();

        return List.of(
                delById("as admin -> ok", seeder.admin(), HttpStatus.NO_CONTENT, seeder.authors().get(4).getId()),
                delById("as moderator -> ok", seeder.moderator(), HttpStatus.NO_CONTENT, seeder.authors().get(5).getId()),
                delById("as user -> ok", seeder.plainUser(), HttpStatus.NO_CONTENT, seeder.plainUserId()),
                delById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, aUserId),
                delById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, aUserId),
                delById("wrong creds -> unauthorized",
                        authorNotInSystem, HttpStatus.UNAUTHORIZED, aUserId),
                delById("another author as user -> forbidden",
                        seeder.creds().get(6), HttpStatus.FORBIDDEN, seeder.adminId())
        );
    }

    private DynamicTest delById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.DELETE)
                .url(Constants.AUTHORS_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

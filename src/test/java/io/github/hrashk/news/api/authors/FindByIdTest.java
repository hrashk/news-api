package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
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

class FindByIdTest extends ControllerTest {
    @Test
    void findById() {
        Credentials a = seeder.moderator();
        Long authorId = seeder.plainUserId();

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.AUTHORS_ID_URL, AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(authorId)
        );
    }

    @Test
    void findMissing() {
        Credentials admin = seeder.admin();
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(admin.username(), admin.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long plainUserId = seeder.plainUserId();

        return List.of(
                findById("as admin -> ok", seeder.admin(), HttpStatus.OK, plainUserId),
                findById("as moderator -> ok", seeder.moderator(), HttpStatus.OK, plainUserId),
                findById("as user -> ok", seeder.plainUser(), HttpStatus.OK, plainUserId),
                findById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, plainUserId),
                findById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, plainUserId),
                findById("wrong creds -> unauthorized",
                        authorNotInSystem, HttpStatus.UNAUTHORIZED, plainUserId),
                findById("another author as user -> forbidden",
                        seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.adminId())
        );
    }

    private DynamicTest findById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.AUTHORS_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

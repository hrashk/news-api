package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
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

class CreateAuthorTest extends ControllerTest {
    @Test
    void create() {
        Credentials c = seeder.moderator();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(c.username(), c.password())
                .postForEntity(Constants.AUTHORS_URL, request, AuthorResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().firstName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody().lastName()).isEqualTo("ipsum")
        );
    }

    @Test
    void creatingWithSameUsernameFails() {
        Credentials c = seeder.moderator();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random.user", "password");

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(c.username(), c.password())
                .postForEntity(Constants.AUTHORS_URL, request, AuthorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<AuthorResponse> response2 = rest.withBasicAuth(c.username(), c.password())
                .postForEntity(Constants.AUTHORS_URL, request, AuthorResponse.class);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createBroken() {
        Credentials c = seeder.moderator();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "  ", null, "random", "password");

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(c.username(), c.password())
                .postForEntity(Constants.AUTHORS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("firstName", "lastName")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        return List.of(
                create("as admin -> created", seeder.admin(), HttpStatus.CREATED, request),
                create("as moderator -> created", seeder.moderator(), HttpStatus.CREATED, request),
                create("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, request),
                create("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request),
                create("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request),
                create("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED, request)
        );
    }

    private DynamicTest create(String message, Credentials creds, HttpStatus status, UpsertAuthorRequest body) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.POST)
                .url(Constants.AUTHORS_URL)
                .credentials(creds)
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

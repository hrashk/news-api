package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.util.ControllerTest;
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

class UpdateAuthorTest extends ControllerTest {
    @Test
    void update() {
        Author a = seeder.plainUser();
        var request = new UpsertAuthorRequest(
                a.getFirstName(), "lorem", "random", "password");

        ResponseEntity<AuthorResponse> response =
                put(Constants.AUTHORS_ID_URL, request, seeder.admin(), AuthorResponse.class, a.getId());

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().lastName()).isEqualTo("lorem"),
                () -> assertThat(response.getBody().username()).isEqualTo("random"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
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

    @TestFactory
    public List<DynamicTest> authorization() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        Author user = seeder.plainUser();
        Long plainUserId = user.getId();
        var request = new UpsertAuthorRequest(
                "random", "last name", user.getUsername(), user.getPassword());

        return List.of(
                update("as admin -> ok", seeder.admin(), HttpStatus.OK, request, plainUserId),
                update("as moderator -> ok", seeder.moderator(), HttpStatus.OK, request, plainUserId),
                update("as user -> ok", user, HttpStatus.OK, request, plainUserId),
                update("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request, plainUserId),
                update("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request, plainUserId),
                update("wrong creds -> unauthorized",
                        authorNotInSystem, HttpStatus.UNAUTHORIZED, request, plainUserId),
                update("another author as user -> forbidden",
                        user, HttpStatus.FORBIDDEN, request, seeder.admin().getId())
        );
    }

    private DynamicTest update(String message, Author authn, HttpStatus status, UpsertAuthorRequest body, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.PUT)
                .url(Constants.AUTHORS_ID_URL)
                .username(authn == null ? null : authn.getUsername())
                .password(authn == null ? null : authn.getPassword())
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

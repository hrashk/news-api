package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
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

class CreateNewsTest extends ControllerTest {
    @Test
    void add() {
        Long authorId = seeder.adminId();
        Long categoryId = seeder.categoryId(4);
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Credentials a = seeder.admin();
        ResponseEntity<NewsResponse> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.NEWS_URL, request, NewsResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().headline()).isEqualTo("h"),
                () -> assertThat(response.getBody().content()).isEqualTo("c")
        );
    }

    @Test
    void createWithInvalidAuthorId() {
        Long authorId = INVALID_ID;
        Long categoryId = seeder.categoryId(4);
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Credentials a = seeder.moderator();
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @Test
    void createWithInvalidCategoryId() {
        Long authorId = seeder.plainUserId();
        Long categoryId = INVALID_ID;
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Credentials a = seeder.plainUser();
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @Test
    void addBroken() {
        UpsertNewsRequest request = new UpsertNewsRequest(null, null, " ", null);

        Credentials a = seeder.plainUser();
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("authorId", "categoryId", "headline", "content")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        return List.of(
                create("as admin -> created", seeder.admin(), HttpStatus.CREATED, seeder.randomNewsRequest(seeder.adminId())),
                create("as moderator -> created", seeder.moderator(), HttpStatus.CREATED, seeder.randomNewsRequest(seeder.moderatorId())),
                create("as user -> created", seeder.plainUser(), HttpStatus.CREATED, seeder.randomNewsRequest(seeder.plainUserId())),
                create("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, seeder.randomNewsRequest(seeder.withoutRolesId())),
                create("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, seeder.randomNewsRequest(INVALID_ID)),
                create("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, seeder.randomNewsRequest(INVALID_ID))
        );
    }

    private DynamicTest create(String message, Credentials creds, HttpStatus status, UpsertNewsRequest body) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.POST)
                .url(Constants.NEWS_URL)
                .credentials(creds)
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

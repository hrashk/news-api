package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.Author;
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

class UpdateNewsTest extends ControllerTest {
    @Test
    void update() {
        var news = seeder.news().get(0);
        var request = upsertRequest(news, "asdf");

        ResponseEntity<NewsResponse> response = put(Constants.NEWS_ID_URL, request,
                seeder.newsAuthorCreds(news), NewsResponse.class, news.getId());

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().headline()).isEqualTo("asdf"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void updateMissing() {
        Long authorId = seeder.authorId(3);
        Long categoryId = seeder.categoryId(4);
        var request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Long newsId = INVALID_ID;
        ResponseEntity<NewsResponse> response = put(Constants.NEWS_ID_URL, request, seeder.moderator(), NewsResponse.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void updateOtherNews() {
        var news = seeder.aNewsNotByAuthor(seeder.moderatorId());
        var request = upsertRequest(news, "asdf");

        ResponseEntity<ErrorInfo> response = put(Constants.NEWS_ID_URL, request, seeder.moderator(),
                ErrorInfo.class, news.getId());

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        var news = seeder.news().get(0);
        var request = upsertRequest(news, "asdf");

        return List.of(
                update("own news -> ok", seeder.newsAuthorCreds(news), HttpStatus.OK, request, news.getId()),
                updateOther("as admin -> forbidden", seeder.admin(), HttpStatus.FORBIDDEN, seeder.adminId()),
                updateOther("as moderator -> forbidden", seeder.moderator(), HttpStatus.FORBIDDEN, seeder.moderatorId()),
                updateOther("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.plainUserId()),
                update("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request, news.getId()),
                update("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request, news.getId()),
                update("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, request, news.getId())
        );
    }

    private static UpsertNewsRequest upsertRequest(News news, String headline) {
        Author author = news.getAuthor();
        Long authorId = author.getId();
        Long categoryId = news.getCategory().getId();

        return new UpsertNewsRequest(authorId, categoryId, headline, news.getContent());
    }

    private DynamicTest updateOther(String message, Credentials creds, HttpStatus status, Long authorId) {
        var news = seeder.aNewsNotByAuthor(authorId);
        var body = upsertRequest(news, "asdf");

        return update("another news " + message, creds, status, body, news.getId());
    }

    private DynamicTest update(String message, Credentials creds, HttpStatus status, UpsertNewsRequest body, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.PUT)
                .url(Constants.NEWS_ID_URL)
                .credentials(creds)
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

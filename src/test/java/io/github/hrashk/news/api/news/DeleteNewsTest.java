package io.github.hrashk.news.api.news;

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

class DeleteNewsTest extends ControllerTest {
    @Test
    void deleteWithComments() {
        var news = seeder.comments().get(0).getNews();
        Long newsId = news.getId();

        Credentials a = seeder.admin();
        ResponseEntity<Void> response = delete(Constants.NEWS_ID_URL, a, newsId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_ID_URL, ErrorInfo.class, newsId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("News")
        );
    }

    @Test
    void deleteMissing() {
        Long newsId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(Constants.NEWS_ID_URL, seeder.moderator(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @Test
    void deleteOthersNews() {
        News news = seeder.aNewsNotByAuthor(seeder.plainUserId());
        Long newsId = news.getId();

        ResponseEntity<ErrorInfo> response = delete(Constants.NEWS_ID_URL, seeder.plainUser(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        News news = seeder.news().get(0);
        Credentials creds = seeder.newsAuthorCreds(news);

        return List.of(
                delById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, news.getId()),
                delById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, news.getId()),
                delById("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, news.getId()),
                deleteOther("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.plainUserId()),
                delById("own news -> ok", creds, HttpStatus.NO_CONTENT, news.getId()),
                deleteOther("as admin -> ok", seeder.admin(), HttpStatus.NO_CONTENT, seeder.adminId()),
                deleteOther("as moderator -> ok", seeder.moderator(), HttpStatus.NO_CONTENT, seeder.moderatorId())
        );
    }

    private DynamicTest deleteOther(String message, Credentials creds, HttpStatus status, Long authorId) {
        var news = seeder.aNewsNotByAuthor(authorId);

        return delById("another news " + message, creds, status, news.getId());
    }

    private DynamicTest delById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.DELETE)
                .url(Constants.NEWS_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

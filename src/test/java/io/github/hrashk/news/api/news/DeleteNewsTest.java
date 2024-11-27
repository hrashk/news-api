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
        Long adminNewsId = seeder.news().get(0).getId();

        return List.of(
                delById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, adminNewsId),
                delById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, adminNewsId),
                delById("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, adminNewsId),
                delById("another news as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, adminNewsId),
                delById("own news -> ok", seeder.plainUser(), HttpStatus.NO_CONTENT, seeder.news().get(2).getId()),
                delById("another news as admin -> ok", seeder.admin(), HttpStatus.NO_CONTENT, seeder.news().get(5).getId()),
                delById("another news as moderator -> ok", seeder.moderator(), HttpStatus.NO_CONTENT, seeder.news().get(6).getId())
        );
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

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsResponse;
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

class FindNewsByIdTest extends ControllerTest {
    @Test
    void findById() {
        Comment comment = seeder.comments().get(0);
        Long newsId = comment.getNews().getId();

        Credentials a = seeder.admin();
        ResponseEntity<NewsResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_ID_URL, NewsResponse.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(newsId),
                () -> assertThat(response.getBody().comments()).anySatisfy(c ->
                        assertThat(c.id()).isEqualTo(comment.getId()))
        );
    }

    @Test
    void findMissing() {
        Long newsId = INVALID_ID;

        Credentials a = seeder.moderator();
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_ID_URL, ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long id = seeder.newsId(0);

        return List.of(
                findById("as admin -> ok", seeder.admin(), HttpStatus.OK, id),
                findById("as moderator -> ok", seeder.moderator(), HttpStatus.OK, id),
                findById("as user -> ok", seeder.plainUser(), HttpStatus.OK, id),
                findById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, id),
                findById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, id),
                findById("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, id)
        );
    }

    private DynamicTest findById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.NEWS_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

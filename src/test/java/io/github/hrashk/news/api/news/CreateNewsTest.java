package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NewsControllerTest extends ControllerTest {
    @Test
    void update() {
        var news = seeder.news().get(0);
        Author author = news.getAuthor();
        Long authorId = author.getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        ResponseEntity<NewsResponse> response = put(Constants.NEWS_ID_URL, request, new Credentials(author), NewsResponse.class, newsId);

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
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Long newsId = INVALID_ID;
        ResponseEntity<NewsResponse> response = put(Constants.NEWS_ID_URL, request, seeder.moderator(), NewsResponse.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @ParameterizedTest
    @CsvSource({Constants.NEWS_ID_URL})
    void updateWithInvalidUser(String url) {
        var news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        ResponseEntity<ErrorInfo> response = put(url, request, seeder.moderator(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }

    @Test
    void deleteWithComments() {
        var news = seeder.comments().get(0).getNews();
        Long newsId = news.getId();

        ResponseEntity<Void> response = delete(Constants.NEWS_ID_URL, seeder.admin(), newsId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(Constants.NEWS_ID_URL, ErrorInfo.class, newsId);
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

    @ParameterizedTest
    @CsvSource({Constants.NEWS_ID_URL})
    void deleteWithInvalidUser(String url) {
        Long newsId = seeder.news().get(0).getId();
        Long userId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(url, seeder.admin(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }
}

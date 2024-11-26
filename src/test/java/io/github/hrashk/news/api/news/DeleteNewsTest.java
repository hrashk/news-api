package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeleteNewsTest extends ControllerTest {
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

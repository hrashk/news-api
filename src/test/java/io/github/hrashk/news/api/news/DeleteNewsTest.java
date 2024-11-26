package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}

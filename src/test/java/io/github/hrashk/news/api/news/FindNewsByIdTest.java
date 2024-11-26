package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}

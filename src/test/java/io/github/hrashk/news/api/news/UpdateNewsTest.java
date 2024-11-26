package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.Author;
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

class UpdateNewsTest extends ControllerTest {
    @Test
    void update() {
        var news = seeder.news().get(0);
        Author author = news.getAuthor();
        Long authorId = author.getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        ResponseEntity<NewsResponse> response = put(Constants.NEWS_ID_URL, request,
                new Credentials(author.getUsername(), seeder.unencodedPassword(author.getUsername())),
                NewsResponse.class, newsId);

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

    @Test
    void updateWithInvalidUser() {
        var news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        ResponseEntity<ErrorInfo> response = put(Constants.NEWS_ID_URL, request, seeder.moderator(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }
}

package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.comments.web.CommentResponse;
import io.github.hrashk.news.api.comments.web.UpsertCommentRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentControllerTest extends ControllerTest {
    private static final String COMMENTS_URL = "/api/v1/comments";
    private static final String COMMENTS_ID_URL = COMMENTS_URL + "/{id}";
    private static final String COMMENTS_WITH_USER_URL = COMMENTS_ID_URL + "?userId={userId}";

    @Test
    void findById() {
        Long commentId = seeder.comments().get(0).getId();

        ResponseEntity<CommentResponse> response = rest.getForEntity(COMMENTS_ID_URL, CommentResponse.class, commentId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().id()).isEqualTo(commentId)
        );
    }

    @Test
    void findMissing() {
        Long commentId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.getForEntity(COMMENTS_ID_URL, ErrorInfo.class, commentId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Comment")
        );
    }

    @Test
    void add() {
        Long newsId = seeder.news().get(0).getId();
        Long authorId = seeder.authors().get(0).getId();
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        ResponseEntity<CommentResponse> response = rest.postForEntity(COMMENTS_URL, request, CommentResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().text()).isEqualTo("lorem")
        );
    }

    @Test
    void addWithInvalidAuthor() {
        Long newsId = seeder.news().get(0).getId();
        Long authorId = INVALID_ID;
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(COMMENTS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @Test
    void addWithInvalidNews() {
        Long newsId = INVALID_ID;
        Long authorId = seeder.authors().get(0).getId();
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(COMMENTS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @Test
    void addBroken() {
        UpsertCommentRequest request = new UpsertCommentRequest(null, null, "  ");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(COMMENTS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("newsId", "authorId", "text")
        );
    }

    @Test
    void update() {
        Comment comment = seeder.comments().get(0);
        Long newsId = comment.getNews().getId();
        Long authorId = comment.getAuthor().getId();
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        Long commentId = comment.getId();
        Long userId = authorId;
        ResponseEntity<CommentResponse> response = put(COMMENTS_WITH_USER_URL,
                request, CommentResponse.class, commentId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().text()).isEqualTo("lorem"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void updateMissing() {
        Long newsId = seeder.news().get(0).getId();
        Long authorId = seeder.authors().get(0).getId();
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        Long commentId = INVALID_ID;
        Long userId = authorId;
        ResponseEntity<CommentResponse> response = put(COMMENTS_WITH_USER_URL,
                request, CommentResponse.class, commentId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @ParameterizedTest
    @CsvSource({COMMENTS_ID_URL, COMMENTS_WITH_USER_URL})
    void updateWithInvalidUser(String url) {
        Comment comment = seeder.comments().get(0);
        Long newsId = comment.getNews().getId();
        Long authorId = comment.getAuthor().getId();
        UpsertCommentRequest request = new UpsertCommentRequest(newsId, authorId, "lorem");

        Long commentId = comment.getId();
        Long userId = INVALID_ID;
        ResponseEntity<ErrorInfo> response = put(url, request, ErrorInfo.class, commentId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }

    @Test
    void delete() {
        Comment comment = seeder.comments().get(0);
        Long commentId = comment.getId();
        Long userId = comment.getAuthor().getId();

        ResponseEntity<Void> response = delete(COMMENTS_WITH_USER_URL, commentId, userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(COMMENTS_ID_URL, ErrorInfo.class, commentId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Comment")
        );
    }

    @Test
    void deleteMissing() {
        Long commentId = INVALID_ID;
        Long userId = seeder.authors().get(0).getId();

        ResponseEntity<ErrorInfo> response = delete(COMMENTS_WITH_USER_URL, ErrorInfo.class, commentId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Comment")
        );
    }

    @ParameterizedTest
    @CsvSource({COMMENTS_ID_URL, COMMENTS_WITH_USER_URL})
    void deleteWithInvalidUser(String url) {
        Long commentId = seeder.comments().get(0).getId();
        Long userId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(url, ErrorInfo.class, commentId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }
}

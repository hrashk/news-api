package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NewsControllerTest extends ControllerTest {
    private static final String NEWS_URL = "/api/v1/news";
    private static final String NEWS_ID_URL = NEWS_URL + "/{id}";
    private static final String NEWS_WITH_USER_URL = NEWS_ID_URL + "?userId={userId}";

    @Test
    void firstPage() {
        ResponseEntity<NewsListResponse> response = rest.getForEntity(NEWS_URL, NewsListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().news()).hasSize(10),
                () -> assertThat(response.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        ResponseEntity<NewsListResponse> response = rest.getForEntity(NEWS_URL + "?page=1&size=3", NewsListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().news()).hasSize(3),
                () -> assertThat(response.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void findByAuthorAndCategory() {
        News news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();

        ResponseEntity<NewsListResponse> entity = rest.getForEntity(NEWS_URL + "?authorId={aid}&categoryId={cid}",
                NewsListResponse.class, authorId, categoryId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n -> assertAll(
                        () -> assertThat(n).hasFieldOrPropertyWithValue("authorId", authorId),
                        () -> assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
                )
        );
    }

    @Test
    void findByAuthor() {
        Long authorId = seeder.news().get(0).getAuthor().getId();

        ResponseEntity<NewsListResponse> entity = rest.getForEntity(NEWS_URL + "?authorId={aid}",
                NewsListResponse.class, authorId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("authorId", authorId))
        );
    }

    @Test
    void findByCategory() {
        Long categoryId = seeder.news().get(0).getCategory().getId();

        ResponseEntity<NewsListResponse> entity = rest.getForEntity(NEWS_URL + "?categoryId={cid}",
                NewsListResponse.class, categoryId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
        );
    }

    @Test
    void findById() {
        Comment comment = seeder.comments().get(0);
        Long newsId = comment.getNews().getId();

        ResponseEntity<NewsResponse> response = rest.getForEntity(NEWS_ID_URL, NewsResponse.class, newsId);

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

        ResponseEntity<ErrorInfo> response = rest.getForEntity(NEWS_ID_URL, ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @Test
    void add() {
        Long authorId = seeder.authors().get(3).getId();
        Long categoryId = seeder.categories().get(4).getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        ResponseEntity<NewsResponse> response = rest.postForEntity(NEWS_URL, request, NewsResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().headline()).isEqualTo("h"),
                () -> assertThat(response.getBody().content()).isEqualTo("c")
        );
    }

    @Test
    void addWithInvalidAuthorId() {
        Long authorId = INVALID_ID;
        Long categoryId = seeder.categories().get(4).getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @Test
    void addiWithInvalidCategoryId() {
        Long authorId = seeder.authors().get(3).getId();
        Long categoryId = INVALID_ID;
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @Test
    void addBroken() {
        UpsertNewsRequest request = new UpsertNewsRequest(null, null, " ", null);

        ResponseEntity<ErrorInfo> response = rest.postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("authorId", "categoryId", "headline", "content")
        );
    }

    @Test
    void update() {
        var news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        Long userId = authorId;
        ResponseEntity<NewsResponse> response = put(NEWS_WITH_USER_URL, request, NewsResponse.class, newsId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().headline()).isEqualTo("asdf"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void updateMissing() {
        Long authorId = seeder.authors().get(3).getId();
        Long categoryId = seeder.categories().get(4).getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Long newsId = INVALID_ID;
        Long userId = authorId;
        ResponseEntity<NewsResponse> response = put(NEWS_WITH_USER_URL, request, NewsResponse.class, newsId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @ParameterizedTest
    @CsvSource({NEWS_ID_URL, NEWS_WITH_USER_URL})
    void updateWithInvalidUser(String url) {
        var news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        Long userId = INVALID_ID;
        ResponseEntity<ErrorInfo> response = put(url, request, ErrorInfo.class, newsId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }

    @Test
    void deleteWithComments() {
        var news = seeder.comments().get(0).getNews();
        Long newsId = news.getId();
        Long userId = news.getAuthor().getId();

        ResponseEntity<Void> response = delete(NEWS_WITH_USER_URL, newsId, userId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(NEWS_ID_URL, ErrorInfo.class, newsId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("News")
        );
    }

    @Test
    void deleteMissing() {
        Long newsId = INVALID_ID;
        Long userId = seeder.authors().get(0).getId();

        ResponseEntity<ErrorInfo> response = delete(NEWS_WITH_USER_URL, ErrorInfo.class, newsId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @ParameterizedTest
    @CsvSource({NEWS_ID_URL, NEWS_WITH_USER_URL})
    void deleteWithInvalidUser(String url) {
        Long newsId = seeder.news().get(0).getId();
        Long userId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(url, ErrorInfo.class, newsId, userId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN),
                () -> assertThat(response.getBody().message()).contains("not allowed")
        );
    }
}

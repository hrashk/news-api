package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.news.web.NewsResponse;
import io.github.hrashk.news.api.news.web.UpsertNewsRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NewsControllerTest extends ControllerTest {
    private static final String NEWS_URL = "/api/v1/news";
    private static final String NEWS_ID_URL = NEWS_URL + "/{id}";

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void firstPage(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_URL, NewsListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().news()).hasSize(10),
                () -> assertThat(response.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties())
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void secondPage(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_URL + "?page=1&size=3", NewsListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().news()).hasSize(3),
                () -> assertThat(response.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties())
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findByAuthorAndCategory(Function<DataSeeder, Author> userProvider, String userType) {
        News news = seeder.news().get(0);
        Long authorId = news.getAuthor().getId();
        Long categoryId = news.getCategory().getId();

        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_URL + "?authorId={aid}&categoryId={cid}",
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

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findByAuthor(Function<DataSeeder, Author> userProvider, String userType) {
        Long authorId = seeder.news().get(0).getAuthor().getId();

        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_URL + "?authorId={aid}", NewsListResponse.class, authorId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("authorId", authorId))
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findByCategory(Function<DataSeeder, Author> userProvider, String userType) {
        Long categoryId = seeder.news().get(0).getCategory().getId();

        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_URL + "?categoryId={cid}", NewsListResponse.class, categoryId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findById(Function<DataSeeder, Author> userProvider, String userType) {
        Comment comment = seeder.comments().get(0);
        Long newsId = comment.getNews().getId();

        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_ID_URL, NewsResponse.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(newsId),
                () -> assertThat(response.getBody().comments()).anySatisfy(c ->
                        assertThat(c.id()).isEqualTo(comment.getId()))
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findMissing(Function<DataSeeder, Author> userProvider, String userType) {
        Long newsId = INVALID_ID;

        Author a = userProvider.apply(seeder);
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(NEWS_ID_URL, ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void add(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);
        Long authorId = a.getId();
        Long categoryId = seeder.categories().get(4).getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        ResponseEntity<NewsResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .postForEntity(NEWS_URL, request, NewsResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().headline()).isEqualTo("h"),
                () -> assertThat(response.getBody().content()).isEqualTo("c")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void addWithInvalidAuthorId(Function<DataSeeder, Author> userProvider, String userType) {
        Long authorId = INVALID_ID;
        Long categoryId = seeder.categories().get(4).getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Author a = userProvider.apply(seeder);
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void addiWithInvalidCategoryId(Function<DataSeeder, Author> userProvider, String userType) {
        Long authorId = seeder.authors().get(3).getId();
        Long categoryId = INVALID_ID;
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "h", "c");

        Author a = userProvider.apply(seeder);
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void addBroken(Function<DataSeeder, Author> userProvider, String userType) {
        UpsertNewsRequest request = new UpsertNewsRequest(null, null, " ", null);

        Author a = userProvider.apply(seeder);
        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .postForEntity(NEWS_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("authorId", "categoryId", "headline", "content")
        );
    }

    @Test
    void update() {
        var news = seeder.news().get(0);
        Author author = news.getAuthor();
        Long authorId = author.getId();
        Long categoryId = news.getCategory().getId();
        UpsertNewsRequest request = new UpsertNewsRequest(authorId, categoryId, "asdf", news.getContent());

        Long newsId = news.getId();
        ResponseEntity<NewsResponse> response = put(NEWS_ID_URL, request, author, NewsResponse.class, newsId);

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
        ResponseEntity<NewsResponse> response = put(NEWS_ID_URL, request, seeder.moderator(), NewsResponse.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @ParameterizedTest
    @CsvSource({NEWS_ID_URL})
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

        ResponseEntity<Void> response = delete(NEWS_ID_URL, seeder.admin(), newsId);
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

        ResponseEntity<ErrorInfo> response = delete(NEWS_ID_URL, seeder.moderator(), ErrorInfo.class, newsId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("News")
        );
    }

    @ParameterizedTest
    @CsvSource({NEWS_ID_URL})
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

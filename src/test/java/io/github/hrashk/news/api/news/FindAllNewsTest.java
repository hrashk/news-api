package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.news.web.NewsListResponse;
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

class FindAllNewsTest extends ControllerTest {
    @Test
    void firstPage() {
        Credentials a = seeder.admin();

        ResponseEntity<NewsListResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_URL, NewsListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().news()).hasSize(10),
                () -> assertThat(response.getBody().news()).allSatisfy(n -> assertThat(n).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        Credentials a = seeder.moderator();

        ResponseEntity<NewsListResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_URL + "?page=1&size=3", NewsListResponse.class);

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

        Credentials a = seeder.plainUser();
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_URL + "?authorId={aid}&categoryId={cid}",
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

        Credentials a = seeder.admin();
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_URL + "?authorId={aid}", NewsListResponse.class, authorId);

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

        Credentials a = seeder.moderator();
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.NEWS_URL + "?categoryId={cid}", NewsListResponse.class, categoryId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        return List.of(
                findAll("as admin -> ok", seeder.admin(), HttpStatus.OK),
                findAll("as moderator -> ok", seeder.moderator(), HttpStatus.OK),
                findAll("as user -> ok", seeder.plainUser(), HttpStatus.OK),
                findAll("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN),
                findAll("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED),
                findAll("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED)
        );
    }

    private DynamicTest findAll(String message, Credentials creds, HttpStatus status) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.NEWS_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

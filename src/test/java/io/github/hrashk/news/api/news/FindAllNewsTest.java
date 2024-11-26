package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.news.web.NewsListResponse;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FindAllNewsTest extends ControllerTest {
    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void firstPage(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(Constants.NEWS_URL, NewsListResponse.class);

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
                .getForEntity(Constants.NEWS_URL + "?page=1&size=3", NewsListResponse.class);

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

    @ParameterizedTest(name = "{1}")
    @MethodSource("users")
    void findByAuthor(Function<DataSeeder, Author> userProvider, String userType) {
        Long authorId = seeder.news().get(0).getAuthor().getId();

        Author a = userProvider.apply(seeder);
        ResponseEntity<NewsListResponse> entity = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(Constants.NEWS_URL + "?authorId={aid}", NewsListResponse.class, authorId);

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
                .getForEntity(Constants.NEWS_URL + "?categoryId={cid}", NewsListResponse.class, categoryId);

        assertAll(
                () -> assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(entity.getBody().news()).isNotEmpty(),
                () -> assertThat(entity.getBody().news()).allSatisfy(n ->
                        assertThat(n).hasFieldOrPropertyWithValue("categoryId", categoryId))
        );
    }
}

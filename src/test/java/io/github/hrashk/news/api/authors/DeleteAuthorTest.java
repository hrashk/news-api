package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeleteAuthorTest extends ControllerTest {
    @ParameterizedTest(name="{1}")
    @MethodSource("adminAndModerator")
    void deleteWithNews(Function<DataSeeder, Credentials> userProvider, String userType) {
        Long authorId = seeder.news().get(0).getAuthor().getId();

        Credentials a = userProvider.apply(seeder);
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, a, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteSelf() {
        Long authorId = seeder.plainUserId();

        Credentials a = seeder.plainUser();
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, a, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest(name="{1}")
    @MethodSource("adminAndModerator")
    void deleteWithComments(Function<DataSeeder, Credentials> userProvider, String userType) {
        Long authorId = seeder.comments().get(0).getAuthor().getId();

        Credentials m = userProvider.apply(seeder);
        ResponseEntity<Void> response = delete(Constants.AUTHORS_ID_URL, m, authorId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(m.username(), m.password())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Author")
        );
    }

    @Test
    void deleteMissing() {
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(Constants.AUTHORS_ID_URL, seeder.admin(), ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }
}

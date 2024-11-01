package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorResponse;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetAuthorByIdTest extends ControllerTest {
    @ParameterizedTest(name="{1}")
    @MethodSource("users")
    void findById(Function<DataSeeder, Author> userProvider, String userType) {
        Author a = userProvider.apply(seeder);
        Long authorId = seeder.plainUser().getId();

        ResponseEntity<AuthorResponse> response = rest.withBasicAuth(a.getUsername(), a.getPassword())
                .getForEntity(Constants.AUTHORS_ID_URL, AuthorResponse.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(authorId)
        );
    }

    @Test
    void plainUserCannotFindOthers() {
        Author pu = seeder.plainUser();
        Long authorId = seeder.withoutRoles().getId();

        ResponseEntity<Map> response = rest.withBasicAuth(pu.getUsername(), pu.getPassword())
                .getForEntity(Constants.AUTHORS_ID_URL, Map.class, authorId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void findMissing() {
        Author admin = seeder.admin();
        Long authorId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_ID_URL, ErrorInfo.class, authorId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Author")
        );
    }
}

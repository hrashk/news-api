package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.AuthorListResponse;
import io.github.hrashk.news.api.util.ControllerTest;
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

class FindAllTest extends ControllerTest {
    @Test
    void firstPage() {
        Author admin = seeder.admin();

        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_URL, AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull()
        );

        assertAll(
                () -> assertThat(response.getBody().authors()).hasSize(10),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        Author admin = seeder.admin();

        ResponseEntity<AuthorListResponse> response = rest.withBasicAuth(admin.getUsername(), admin.getPassword())
                .getForEntity(Constants.AUTHORS_URL + "?page=1&size=3", AuthorListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotNull()
        );

        assertAll(
                () -> assertThat(response.getBody().authors()).hasSize(3),
                () -> assertThat(response.getBody().authors()).allSatisfy(a -> assertThat(a).hasNoNullFieldsOrProperties())
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        return List.of(
                findAll("as admin -> ok", seeder.admin(), HttpStatus.OK),
                findAll("as moderator -> forbidden", seeder.moderator(), HttpStatus.FORBIDDEN),
                findAll("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN),
                findAll("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN),
                findAll("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED),
                findAll("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED)
        );
    }

    private DynamicTest findAll(String message, Author authn, HttpStatus status) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.AUTHORS_URL)
                .authn(authn)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

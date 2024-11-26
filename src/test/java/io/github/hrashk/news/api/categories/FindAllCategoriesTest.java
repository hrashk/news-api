package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.categories.web.CategoryListResponse;
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

class FindAllCategoriesTest extends ControllerTest {
    @Test
    void firstPage() {
        Credentials a = seeder.admin();

        ResponseEntity<CategoryListResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.CATEGORIES_URL, CategoryListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().categories()).hasSize(10),
                () -> assertThat(response.getBody().categories()).allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        Credentials a = seeder.moderator();

        ResponseEntity<CategoryListResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.CATEGORIES_URL + "?page=1&size=3", CategoryListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().categories()).hasSize(3),
                () -> assertThat(response.getBody().categories()).allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties())
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
                findAll("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED)
        );
    }

    private DynamicTest findAll(String message, Credentials creds, HttpStatus status) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.CATEGORIES_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

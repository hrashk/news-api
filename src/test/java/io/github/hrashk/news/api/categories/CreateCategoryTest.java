package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.categories.web.UpsertCategoryRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
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

class CreateCategoryTest extends ControllerTest {
    @Test
    void create() {
        Credentials a = seeder.admin();

        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.CATEGORIES_URL, request, CategoryResponse.class);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem")
        );
    }

    @Test
    void createBroken() {
        Credentials a = seeder.moderator();

        var request = new UpsertCategoryRequest("  ");

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.CATEGORIES_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("name")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        return List.of(
                create("as admin -> created", seeder.admin(), HttpStatus.CREATED, seeder.randomCategoryRequest()),
                create("as moderator -> created", seeder.moderator(), HttpStatus.CREATED, seeder.randomCategoryRequest()),
                create("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.randomCategoryRequest()),
                create("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, seeder.randomCategoryRequest()),
                create("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, seeder.randomCategoryRequest()),
                create("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, seeder.randomCategoryRequest())
        );
    }

    private DynamicTest create(String message, Credentials creds, HttpStatus status, UpsertCategoryRequest body) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.POST)
                .url(Constants.CATEGORIES_URL)
                .credentials(creds)
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .build());
    }
}

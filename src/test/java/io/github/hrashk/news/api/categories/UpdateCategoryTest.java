package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.categories.web.UpsertCategoryRequest;
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

class UpdateCategoryTest extends ControllerTest {
    @Test
    void update() {
        Credentials a = seeder.admin();

        Long categoryId = seeder.categories().get(0).getId();
        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response =
                put(Constants.CATEGORIES_ID_URL, request, a, CategoryResponse.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem")
        );
    }

    @Test
    void updateMissing() {
        Long categoryId = INVALID_ID;
        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response =
                put(Constants.CATEGORIES_ID_URL, request, seeder.moderator(), CategoryResponse.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long id = seeder.categories().get(0).getId();
        var request = new UpsertCategoryRequest("lorem");

        return List.of(
                update("as admin -> ok", seeder.admin(), HttpStatus.OK, request, id),
                update("as moderator -> ok", seeder.moderator(), HttpStatus.OK, request, id),
                update("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, request, id),
                update("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request, id),
                update("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request, id),
                update("wrong creds -> unauthorized", seeder.fakeUser(), HttpStatus.UNAUTHORIZED, request, id)
        );
    }

    private DynamicTest update(String message, Credentials creds, HttpStatus status, UpsertCategoryRequest body, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.PUT)
                .url(Constants.CATEGORIES_ID_URL)
                .credentials(creds)
                .body(body)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

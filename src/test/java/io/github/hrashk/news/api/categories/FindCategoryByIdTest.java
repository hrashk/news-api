package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
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

class FindCategoryByIdTest extends ControllerTest {
    @Test
    void findById() {
        Credentials a = seeder.moderator();

        Long categoryId = seeder.categories().get(0).getId();

        ResponseEntity<CategoryResponse> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.CATEGORIES_ID_URL, CategoryResponse.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(categoryId)
        );
    }

    @Test
    void findMissing() {
        Credentials a = seeder.plainUser();

        Long categoryId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.CATEGORIES_ID_URL, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long categoryId = seeder.categories().get(0).getId();

        return List.of(
                findById("as admin -> ok", seeder.admin(), HttpStatus.OK, categoryId),
                findById("as moderator -> ok", seeder.moderator(), HttpStatus.OK, categoryId),
                findById("as user -> ok", seeder.plainUser(), HttpStatus.OK, categoryId),
                findById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, categoryId),
                findById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, categoryId),
                findById("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED, categoryId)
        );
    }

    private DynamicTest findById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.GET)
                .url(Constants.CATEGORIES_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

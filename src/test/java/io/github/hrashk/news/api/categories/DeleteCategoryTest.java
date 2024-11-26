package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
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

class DeleteCategoryTest extends ControllerTest {
    @Test
    void deleteWithNews() {
        Credentials a = seeder.admin();

        Long categoryId = seeder.news().get(0).getCategory().getId();

        ResponseEntity<ErrorInfo> response =
                delete(Constants.CATEGORIES_ID_URL, a, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("Cannot")
        );
    }

    @Test
    void deleteMissing() {
        Long categoryId = INVALID_ID;

        ResponseEntity<ErrorInfo> response =
                delete(Constants.CATEGORIES_ID_URL, seeder.admin(), ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @TestFactory
    public List<DynamicTest> authorization() {
        Long id = seeder.categoryId(2);

        return List.of(
                delById("as admin -> ok", seeder.admin(), HttpStatus.NO_CONTENT, seeder.categoryId(0)),
                delById("as moderator -> ok", seeder.moderator(), HttpStatus.NO_CONTENT, seeder.categoryId(1)),
                delById("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, id),
                delById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, id),
                delById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, id),
                delById("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED, id)
        );
    }

    private DynamicTest delById(String message, Credentials creds, HttpStatus status, Long id) {
        return dynamicTest(message, HttpExecutable.builder()
                .method(HttpMethod.DELETE)
                .url(Constants.CATEGORIES_ID_URL)
                .credentials(creds)
                .expectedStatus(status)
                .rest(rest)
                .urlVariable(id)
                .build());
    }
}

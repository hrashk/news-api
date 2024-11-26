package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.categories.web.UpsertCategoryRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryControllerTest extends ControllerTest {
    static Stream<Arguments> upsertUsers() {
        return Stream.of(
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::admin, "admin"),
                Arguments.of((Function<DataSeeder, Credentials>) DataSeeder::moderator, "moderator"));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("upsertUsers")
    void addThenDelete(Function<DataSeeder, Credentials> userProvider, String userType) {
        Credentials a = userProvider.apply(seeder);

        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.CATEGORIES_URL, request, CategoryResponse.class);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem")
        );

        Long id = response.getBody().id();
        ResponseEntity<Void> deleteResponse = delete(Constants.CATEGORIES_ID_URL, a, id);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.withBasicAuth(a.username(), a.password())
                .getForEntity(Constants.CATEGORIES_ID_URL, ErrorInfo.class, id);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Category")
        );
    }

    @Test
    void addBroken() {
        Credentials a = seeder.moderator();

        var request = new UpsertCategoryRequest("  ");

        ResponseEntity<ErrorInfo> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.CATEGORIES_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("name")
        );
    }

    @Test
    void plainUserCannotAdd() {
        Credentials a = seeder.plainUser();

        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<?> response = rest.withBasicAuth(a.username(), a.password())
                .postForEntity(Constants.CATEGORIES_URL, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("upsertUsers")
    void update(Function<DataSeeder, Credentials> userProvider, String userType) {
        Credentials a = userProvider.apply(seeder);

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
    void plainUserCannotUpdate() {
        Credentials a = seeder.plainUser();

        Long categoryId = seeder.categories().get(0).getId();
        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<?> response =
                put(Constants.CATEGORIES_ID_URL, request, a, Map.class, categoryId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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

    @ParameterizedTest(name = "{1}")
    @MethodSource("upsertUsers")
    void deleteWithNews(Function<DataSeeder, Credentials> userProvider, String userType) {
        Credentials a = userProvider.apply(seeder);

        Long categoryId = seeder.news().get(0).getCategory().getId();

        ResponseEntity<ErrorInfo> response =
                delete(Constants.CATEGORIES_ID_URL, a, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("Cannot")
        );
    }

    @Test
    void plainUserCannotDelete() {
        Credentials a = seeder.plainUser();

        Long categoryId = seeder.news().get(0).getCategory().getId();

        ResponseEntity<?> response =
                delete(Constants.CATEGORIES_ID_URL, a, Map.class, categoryId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
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
}

package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.categories.web.CategoryListResponse;
import io.github.hrashk.news.api.categories.web.CategoryResponse;
import io.github.hrashk.news.api.categories.web.UpsertCategoryRequest;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryControllerTest extends ControllerTest {

    private static final String CATEGORIES_URL = "/api/v1/categories";
    private static final String CATEGORIES_ID_URL = CATEGORIES_URL + "/{id}";

    @Test
    void firstPage() {
        ResponseEntity<CategoryListResponse> response = rest.getForEntity(CATEGORIES_URL, CategoryListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().categories()).hasSize(10),
                () -> assertThat(response.getBody().categories()).allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void secondPage() {
        ResponseEntity<CategoryListResponse> response = rest.getForEntity(CATEGORIES_URL + "?page=1&size=3", CategoryListResponse.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody().categories()).hasSize(3),
                () -> assertThat(response.getBody().categories()).allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties())
        );
    }

    @Test
    void findById() {
        Long categoryId = seeder.categories().get(0).getId();

        ResponseEntity<CategoryResponse> response = rest.getForEntity(CATEGORIES_ID_URL, CategoryResponse.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().id()).isEqualTo(categoryId)
        );
    }

    @Test
    void findMissing() {
        Long categoryId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = rest.getForEntity(CATEGORIES_ID_URL, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }

    @Test
    void addThenDelete() {
        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response = rest.postForEntity(CATEGORIES_URL, request, CategoryResponse.class);
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties(),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem")
        );

        Long id = response.getBody().id();
        ResponseEntity<Void> deleteResponse = delete(CATEGORIES_ID_URL, id);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorInfo> findResponse = rest.getForEntity(CATEGORIES_ID_URL, ErrorInfo.class, id);
        assertAll(
                () -> assertThat(findResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(findResponse.getBody().message()).contains("Category")
        );
    }

    @Test
    void addBroken() {
        var request = new UpsertCategoryRequest("  ");

        ResponseEntity<ErrorInfo> response = rest.postForEntity(CATEGORIES_URL, request, ErrorInfo.class);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("name")
        );
    }

    @Test
    void update() {
        Long categoryId = seeder.categories().get(0).getId();
        var request = new UpsertCategoryRequest("lorem");

        ResponseEntity<CategoryResponse> response = put(CATEGORIES_ID_URL, request, CategoryResponse.class, categoryId);

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

        ResponseEntity<CategoryResponse> response = put(CATEGORIES_ID_URL, request, CategoryResponse.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED),
                () -> assertThat(response.getBody().name()).isEqualTo("lorem"),
                () -> assertThat(response.getBody()).hasNoNullFieldsOrProperties()
        );
    }

    @Test
    void deleteWithNews() {
        Long categoryId = seeder.news().get(0).getCategory().getId();

        ResponseEntity<ErrorInfo> response = delete(CATEGORIES_ID_URL, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().message()).contains("Cannot")
        );
    }

    @Test
    void deleteMissing() {
        Long categoryId = INVALID_ID;

        ResponseEntity<ErrorInfo> response = delete(CATEGORIES_ID_URL, ErrorInfo.class, categoryId);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(response.getBody().message()).contains("Category")
        );
    }
}

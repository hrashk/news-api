package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.exceptions.ErrorInfo;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.Credentials;
import io.github.hrashk.news.api.util.DataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeleteCategoryTest extends ControllerTest {
    @Test
    void deleteWithNews(Function<DataSeeder, Credentials> userProvider, String userType) {
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

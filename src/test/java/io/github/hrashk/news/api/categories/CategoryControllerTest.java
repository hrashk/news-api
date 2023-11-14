package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.categories.web.CategoryController;
import io.github.hrashk.news.api.categories.web.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    private static final long VALID_ID = 7L;
    private static final long INVALID_ID = 522L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CategoryJsonSamples json;
    @MockBean
    private CategoryService service;

    @TestConfiguration
    @Import({CategoryJsonSamples.class})
    static class AppConfig {
        @Bean
        CategoryMapper mapper() {
            return Mappers.getMapper(CategoryMapper.class);
        }
    }

    @Test
    void getAllCategories() throws Exception {
        Mockito.when(service.findAll()).thenReturn(CategorySamples.twoCategories());

        mvc.perform(get("/api/v1/categories"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.findAllResponse(), true)
                );
    }

    @Test
    void findByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(CategorySamples.sciFi());

        mvc.perform(get("/api/v1/categories/" + VALID_ID))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/v1/categories/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void addCategory() throws Exception {
        Mockito.when(service.addOrReplace(Mockito.any(Category.class))).thenReturn(CategorySamples.sciFi());

        mvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Category id").isNull()));
    }

    @Test
    void updateByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(CategorySamples.sciFi());
        Mockito.when(service.addOrReplace(Mockito.any(Category.class))).thenReturn(CategorySamples.sciFi());

        mvc.perform(put("/api/v1/categories/" + VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a -> Assertions.assertAll(
                () -> assertThat(a.getId()).as("Category id").isEqualTo(VALID_ID),
                () -> assertThat(a.getCreatedAt()).as("Category created at").isNotNull()
        )));
    }

    @Test
    void updateByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);
        Mockito.when(service.addOrReplace(Mockito.any(Category.class))).thenReturn(CategorySamples.sciFi());

        mvc.perform(put("/api/v1/categories/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Category id").isNull()));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete("/api/v1/categories/" + VALID_ID))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(VALID_ID));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete("/api/v1/categories/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

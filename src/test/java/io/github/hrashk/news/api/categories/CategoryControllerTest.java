package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(CategoryJsonSamples.class)
class CategoryControllerTest extends ControllerTest {
    @Autowired
    private CategoryJsonSamples json;

    public String categoriesUrl() {
        return "/api/v1/categories";
    }

    public String categoriesUrl(Long id) {
        return categoriesUrl() + "/" + id;
    }

    @Test
    void getFirstPageOfCategories() throws Exception {
        when(categoryService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoCategories());

        mvc.perform(get(categoriesUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );
    }

    @Test
    void getSecondPageOfCategories() throws Exception {
        when(categoryService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoCategories());

        mvc.perform(get(categoriesUrl()).param("page", "1").param("size", "7"))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );
    }

    @Test
    void findByValidId() throws Exception {
        when(categoryService.findById(Mockito.anyLong())).thenReturn(samples.sciFi());

        mvc.perform(get(categoriesUrl(7L)))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.upsertResponse(), true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(1L));

        mvc.perform(get(categoriesUrl(7L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }

    @Test
    void addCategory() throws Exception {
        mvc.perform(post(categoriesUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(categoryService).addOrReplace(Mockito.assertArg(c ->
                assertThat(c).hasFieldOrPropertyWithValue("id", null)
        ));
    }

    @Test
    void updateCategory() throws Exception {
        mvc.perform(put(categoriesUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(categoryService).addOrReplace(Mockito.assertArg(c ->
                assertThat(c).hasFieldOrPropertyWithValue("id", 7L)
        ));
    }

    @Test
    void updateWithInvalidIdCreatesNewEntity() throws Exception {
        when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(1L));

        mvc.perform(put(categoriesUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(categoryService).addOrReplace(Mockito.assertArg(c ->
                assertThat(c).hasFieldOrPropertyWithValue("id", null)
        ));
    }

    @Test
    void deleteByValidId() throws Exception {
        mvc.perform(delete(categoriesUrl(7L)))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(categoryService).delete(Mockito.assertArg(c ->
                assertThat(c).hasFieldOrPropertyWithValue("id", 7L)
        ));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(7L));

        mvc.perform(delete(categoriesUrl(7L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }
}

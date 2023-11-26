package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Assertions;
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

@Import(AuthorJsonSamples.class)
class AuthorControllerTest extends ControllerTest {
    @Autowired
    private AuthorJsonSamples json;

    public String authorsUrl() {
        return "/api/v1/authors";
    }

    public String authorsUrl(Long id) {
        return authorsUrl() + "/" + id;
    }

    @Test
    void getFirstPageOfAuthors() throws Exception {
        when(authorService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoAuthors());

        mvc.perform(get(authorsUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(authorService).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(0),
                () -> assertThat(p.getPageSize()).isEqualTo(10)
        )));
    }

    @Test
    void getSecondPageOfAuthors() throws Exception {
        when(authorService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoAuthors());

        mvc.perform(get(authorsUrl()).param("page", "1").param("size", "7"))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(authorService).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(1),
                () -> assertThat(p.getPageSize()).isEqualTo(7)
        )));
    }

    @Test
    void findByValidId() throws Exception {
        when(authorService.findById(Mockito.anyLong())).thenReturn(samples.jackDoe());

        mvc.perform(get(authorsUrl(21L)))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.upsertResponse(), true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
//        when(authorService.findById(Mockito.anyLong())).thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(get(authorsUrl(-1L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void addAuthor() throws Exception {
        mvc.perform(post(authorsUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.upsertResponse(), true)
                );

//        Mockito.verify(authorService).addOrReplace(Mockito.assertArg(a ->
//                assertThat(a).hasFieldOrPropertyWithValue("id", null)
//        ));
    }

    @Test
    void updateAuthor() throws Exception {
        mvc.perform(put(authorsUrl(21L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.upsertResponse(), true)
                );

//        Mockito.verify(authorService).addOrReplace(Mockito.assertArg(a ->
//                assertThat(a).hasFieldOrPropertyWithValue("id", 21L)
//        ));
    }

    @Test
    void updatingWithInvalidIdCreatesNewEntity() throws Exception {
//        when(authorService.findById(anyLong())).thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(put(authorsUrl(-1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.upsertResponse(), true)
                );

//        Mockito.verify(authorService).addOrReplace(Mockito.assertArg(a ->
//                assertThat(a).hasFieldOrPropertyWithValue("id", null)
//        ));
    }

    @Test
    void deleteByValidId() throws Exception {
        mvc.perform(delete(authorsUrl(21L)))
                .andExpect(status().isNoContent());

//        Mockito.verify(authorService).delete(Mockito.assertArg(a ->
//                assertThat(a).hasFieldOrPropertyWithValue("id", 21L)
//        ));
    }

    @Test
    void deletingByInvalidIdFails() throws Exception {
//        when(authorService.findById(Mockito.anyLong())).thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(delete(authorsUrl(-1L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }
}

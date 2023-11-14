package io.github.hrashk.news.api.authors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorControllerTest extends ControllerTestDependencies {

    private static final long VALID_ID = 3L;
    private static final long INVALID_ID = 713L;

    @Test
    void getAllAuthors() throws Exception {
        Mockito.when(service.findAll()).thenReturn(AuthorSamples.twoAuthors());

        mvc.perform(get("/api/v1/authors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(findAllResponse, true)
                );
    }

    @Test
    void addAuthor() throws Exception {
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(upsertRequest))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(upsertResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Author id").isNull()));
    }

    @Test
    void findByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(get("/api/v1/authors/" + VALID_ID))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(upsertResponse, true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/v1/authors/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void updateByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(AuthorSamples.jackDoe());
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(put("/api/v1/authors/" + VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(upsertRequest))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(upsertResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a -> Assertions.assertAll(
                () -> assertThat(a.getId()).as("Author id").isEqualTo(VALID_ID),
                () -> assertThat(a.getCreatedAt()).as("Author created at").isNotNull()
        )));
    }

    @Test
    void updateByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(put("/api/v1/authors/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(upsertRequest))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(upsertResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Author id").isNull()));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete("/api/v1/authors/" + VALID_ID))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(VALID_ID));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete("/api/v1/authors/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.authors.web.AuthorController;
import io.github.hrashk.news.api.authors.web.AuthorMapper;
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

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected AuthorJsonSamples json;
    @Autowired
    protected AuthorSamples samples;
    @MockBean
    protected AuthorService service;

    @TestConfiguration
    @Import({AuthorJsonSamples.class, AuthorSamples.class})
    static class AppConfig {
        @Bean
        AuthorMapper mapper() {
            return Mappers.getMapper(AuthorMapper.class);
        }
    }

    @Test
    void getAllAuthors() throws Exception {
        Mockito.when(service.findAll()).thenReturn(samples.twoAuthors());

        mvc.perform(get(samples.baseUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.findAllResponse(), true)
                );
    }

    @Test
    void addAuthor() throws Exception {
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(samples.jackDoe());

        mvc.perform(post(samples.baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Author id").isNull()));
    }

    @Test
    void findByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(samples.jackDoe());

        mvc.perform(get(samples.validAuthorUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenThrow(NoSuchElementException.class);

        mvc.perform(get(samples.invalidAuthorUrl()))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void updateByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(samples.validId()))).thenReturn(samples.jackDoe());
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(samples.jackDoe());

        mvc.perform(put(samples.validAuthorUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a -> Assertions.assertAll(
                () -> assertThat(a.getId()).as("Author id").isEqualTo(samples.validId()),
                () -> assertThat(a.getCreatedAt()).as("Author created at").isNotNull()
        )));
    }

    @Test
    void updateByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(samples.invalidId()))).thenThrow(NoSuchElementException.class);
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(samples.jackDoe());

        mvc.perform(put(samples.invalidAuthorUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Author id").isNull()));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete(samples.validAuthorUrl()))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(samples.validId()));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete(samples.invalidAuthorUrl()))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

package io.github.hrashk.news.api.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hrashk.news.api.authors.web.AuthorController;
import io.github.hrashk.news.api.authors.web.AuthorMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    private static final long VALID_ID = 3L;
    private static final long INVALID_ID = 713L;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService service;

    @TestConfiguration
    static class AppConfig {
        @Bean
        AuthorMapper mapper() {
            return Mappers.getMapper(AuthorMapper.class);
        }
    }

    @Test
    void getAllAuthors(@Value("classpath:authors/find_all_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findAll()).thenReturn(AuthorSamples.twoAuthors());

        mvc.perform(get("/api/v1/authors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
                );
    }

    @Test
    void addAuthor(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        String requestPayload = objectMapper.writeValueAsString(AuthorSamples.jackDoeRequest());
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Author id").isNull()));
    }

    @Test
    void findByValidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(get("/api/v1/authors/" + VALID_ID))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
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
    void updateByValidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(AuthorSamples.jackDoeRequest());
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(AuthorSamples.jackDoe());
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(put("/api/v1/authors/" + VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a -> Assertions.assertAll(
                () -> assertThat(a.getId()).as("Author id").isEqualTo(VALID_ID),
                () -> assertThat(a.getCreatedAt()).as("Author created at").isNotNull()
        )));
    }

    @Test
    void updateByInvalidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(AuthorSamples.jackDoeRequest());
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(AuthorSamples.jackDoe());

        mvc.perform(put("/api/v1/authors/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
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

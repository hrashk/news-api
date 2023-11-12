package io.github.hrashk.news.api.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hrashk.news.api.authors.web.AuthorsController;
import io.github.hrashk.news.api.authors.web.AuthorsMapper;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
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

@WebMvcTest(AuthorsController.class)
class AuthorsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService service;

    @TestConfiguration
    static class AppConfig {
        @Bean
        AuthorsMapper mapper() {
            return Mappers.getMapper(AuthorsMapper.class);
        }
    }

    @Test
    void getAllAuthors(@Value("classpath:authors/find_all_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findAll()).thenReturn(TestData.twoAuthors());

        mvc.perform(get("/api/v1/authors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse)
                );
    }

    @Test
    void addAuthor(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        String requestPayload = objectMapper.writeValueAsString(new UpsertAuthorRequest("Jack", "Doe"));
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(TestData.jackDoe());

        mvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a).hasFieldOrPropertyWithValue("id", null)));
    }

    @Test
    void findByValidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(TestData.jackDoe());

        mvc.perform(get("/api/v1/authors/3"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.anyLong())).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/v1/authors/713"))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void updateByValidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(new UpsertAuthorRequest("Jack", "Doe"));
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(TestData.jackDoe());
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(put("/api/v1/authors/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a).hasFieldOrPropertyWithValue("id", 3L)));
    }

    @Test
    void updateByInvalidId(@Value("classpath:authors/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(new UpsertAuthorRequest("Jack", "Doe"));
        Mockito.when(service.addOrReplace(Mockito.any(Author.class))).thenReturn(TestData.jackDoe());
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(put("/api/v1/authors/713")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a).hasFieldOrPropertyWithValue("id", 713L)));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete("/api/v1/authors/3"))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(3L));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete("/api/v1/authors/713"))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

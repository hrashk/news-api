package io.github.hrashk.news.api.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        String expectedPayload = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findAll()).thenReturn(TestData.twoAuthors());

        mvc.perform(get("/api/v1/authors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedPayload)
                );
    }

    @Test
    void addAuthor(@Value("classpath:authors/create_response.json") Resource r) throws Exception {
        String expectedPayload = r.getContentAsString(StandardCharsets.UTF_8);
        var request = new UpsertAuthorRequest("Jack", "Doe");
        Mockito.when(service.save(Mockito.any(Author.class))).thenReturn(TestData.jackDoe());

        mvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedPayload)
                );
    }

    @Test
    void findByExistingId(@Value("classpath:authors/create_response.json") Resource r) throws Exception {
        String expectedPayload = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(TestData.jackDoe());

        mvc.perform(get("/api/v1/authors/3"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedPayload)
                );
    }
}

package io.github.hrashk.news.api.authors;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorsController.class)
class AuthorsControllerTest {

    @Autowired
    private MockMvc mvc;

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
    void getAllAuthors(@Value("classpath:authors/authors.json") Resource r) throws Exception {
        String expectedPayload = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findAll()).thenReturn(TestData.twoAuthors());

        mvc.perform(get("/authors"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedPayload)
                );
    }
}

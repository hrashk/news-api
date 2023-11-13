package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.news.web.NewsController;
import io.github.hrashk.news.api.news.web.NewsMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
class NewsControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private NewsService service;

    @TestConfiguration
    static class AppConfig {
        @Bean
        NewsMapper mapper() {
            return Mappers.getMapper(NewsMapper.class);
        }
    }

    @Test
    void getAllNews() throws Exception {
        String expectedResponse = "{}";

        mvc.perform(get("/api/v1/news"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(expectedResponse, true)
                );
    }
}

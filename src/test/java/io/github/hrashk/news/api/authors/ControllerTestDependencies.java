package io.github.hrashk.news.api.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hrashk.news.api.authors.web.AuthorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebMvcTest(AuthorController.class)
@Import(MapperBeans.class)
abstract class ControllerTestDependencies {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected AuthorService service;

    protected String findAllResponse;

    protected String upsertResponse;

    @Value("classpath:authors/find_all_response.json")
    void setFindAllResponse(Resource r) throws IOException {
        findAllResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:authors/upsert_response.json")
    void setUpsertResponse(Resource r) throws IOException {
        upsertResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

}

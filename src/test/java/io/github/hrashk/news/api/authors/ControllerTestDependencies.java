package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.authors.web.AuthorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthorController.class)
@Import({MapperBeans.class, AuthorJsonSamples.class})
abstract class ControllerTestDependencies {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected AuthorJsonSamples json;
    @MockBean
    protected AuthorService service;
}

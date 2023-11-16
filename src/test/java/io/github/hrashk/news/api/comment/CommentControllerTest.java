package io.github.hrashk.news.api.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.comments.web.CommentController;
import io.github.hrashk.news.api.comments.web.CommentMapper;
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

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    private static final long VALID_ID = 7L;
    private static final long INVALID_ID = 522L;

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CommentService service;
    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class AppConfig {
        @Bean
        CommentMapper mapper() {
            return Mappers.getMapper(CommentMapper.class);
        }
    }

    @Test
    void getAllComments(@Value("classpath:comments/find_all_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findAll()).thenReturn(CommentSamples.twoComments());

        mvc.perform(get("/api/v1/comments"))
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponse, true)
                );
    }

    @Test
    void findByValidId(@Value("classpath:comments/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(CommentSamples.smiley());

        mvc.perform(get("/api/v1/comments/" + VALID_ID))
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponse, true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/v1/comments/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void addComment(@Value("classpath:comments/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);
        String requestPayload = objectMapper.writeValueAsString(CommentSamples.smileyRequest());
        Mockito.when(service.addOrReplace(Mockito.any(Comment.class))).thenReturn(CommentSamples.smiley());

        mvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().json(expectedResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Comment id").isNull()));
    }

    @Test
    void updateByValidId(@Value("classpath:comments/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(CommentSamples.smileyRequest());
        Mockito.when(service.findById(Mockito.eq(VALID_ID))).thenReturn(CommentSamples.smiley());
        Mockito.when(service.addOrReplace(Mockito.any(Comment.class))).thenReturn(CommentSamples.smiley());

        mvc.perform(put("/api/v1/comments/" + VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isOk(),
                        content().json(expectedResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a -> Assertions.assertAll(
                () -> assertThat(a.getId()).as("Comment id").isEqualTo(VALID_ID),
                () -> assertThat(a.getCreatedAt()).as("Comment created at").isNotNull()
        )));
    }

    @Test
    void updateByInvalidId(@Value("classpath:comments/upsert_response.json") Resource r) throws Exception {
        String expectedResponse = r.getContentAsString(StandardCharsets.UTF_8);

        String requestPayload = objectMapper.writeValueAsString(CommentSamples.smileyRequest());
        Mockito.when(service.findById(Mockito.eq(INVALID_ID))).thenThrow(NoSuchElementException.class);
        Mockito.when(service.addOrReplace(Mockito.any(Comment.class))).thenReturn(CommentSamples.smiley());

        mvc.perform(put("/api/v1/comments/" + INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload))
                .andExpectAll(
                        status().isCreated(),
                        content().json(expectedResponse, true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(a ->
                assertThat(a.getId()).as("Comment id").isNull()));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete("/api/v1/comments/" + VALID_ID))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(VALID_ID));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete("/api/v1/comments/" + INVALID_ID))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.authors.AuthorNotFoundException;
import io.github.hrashk.news.api.news.NewsNotFoundException;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({CommentJsonSamples.class})
class CommentControllerTest extends ControllerTest {
    @Autowired
    private CommentJsonSamples jsonSamples;

    String commentsUrl() {
        return "/api/v1/comments";
    }

    String commentsUrl(Long id) {
        return commentsUrl() + "/" + id;
    }

    @Test
    void getCommentById() throws Exception {
        var comments = new ArrayList<>(samples.greatNews().getComments());
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenReturn(comments.get(0));

        mvc.perform(get(commentsUrl(3L)))
                .andExpectAll(
                        status().isOk(),
                        content().json(jsonSamples.upsertResponse())
                );
    }

    @Test
    void getMissingComment() throws Exception {
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenThrow(new CommentNotFoundException(1L));

        mvc.perform(get(commentsUrl(3L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Comment"))
                );
    }

    @Test
    void addComment() throws Exception {
        mvc.perform(post(commentsUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(jsonSamples.upsertResponse())
                );

//        Mockito.verify(commentService).addOrReplace(Mockito.assertArg(c ->
//                assertThat(c).hasFieldOrPropertyWithValue("id",null)
//        ));
    }

    @Test
    void addCommentWithInvalidAuthor() throws Exception {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(post(commentsUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void addCommentWithInvalidNews() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenThrow(new NewsNotFoundException(1L));

        mvc.perform(post(commentsUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("News"))
                );
    }

    @Test
    void updateComment() throws Exception {
        mvc.perform(put(commentsUrl(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().json(jsonSamples.upsertResponse())
                );

//        Mockito.verify(commentService).addOrReplace(Mockito.assertArg(c ->
//                assertThat(c).hasFieldOrPropertyWithValue("id",3L)
//        ));
    }

    @Test
    void updateMissingComment() throws Exception {
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenThrow(CommentNotFoundException.class);

        mvc.perform(put(commentsUrl(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(jsonSamples.upsertResponse())
                );

//        Mockito.verify(commentService).addOrReplace(Mockito.assertArg(c ->
//                assertThat(c).hasFieldOrPropertyWithValue("id",null)
//        ));
    }

    @Test
    void updateCommentWithInvalidAuthor() throws Exception {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(put(commentsUrl(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void updateCommentWithInvalidNews() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenThrow(new NewsNotFoundException(1L));

        mvc.perform(put(commentsUrl(3L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSamples.upsertRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("News"))
                );
    }

    @Test
    void deleteComment() throws Exception {
        mvc.perform(delete(commentsUrl(3L)))
                .andExpectAll(
                        status().isNoContent()
                );

//        Mockito.verify(commentService).delete(Mockito.assertArg(c ->
//                assertThat(c).hasFieldOrPropertyWithValue("id",3L)
//        ));
    }

    @Test
    void deleteMissingComment() throws Exception {
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenThrow(new CommentNotFoundException(1L));

        mvc.perform(delete(commentsUrl(3L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Comment"))
                );
    }
}
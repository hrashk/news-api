package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.AuthorNotFoundException;
import io.github.hrashk.news.api.categories.CategoryNotFoundException;
import io.github.hrashk.news.api.util.AssertionHelpers;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({NewsSamples.class, NewsJsonSamples.class})
class NewsControllerTest extends ControllerTest {
    @Autowired
    private NewsJsonSamples json;
    @Autowired
    private NewsSamples samples;

    @Test
    void firstPageOfNews() throws Exception {
        when(newsService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoNews());

        mvc.perform(get(samples.baseUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(newsService).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(0),
                () -> assertThat(p.getPageSize()).isEqualTo(10)
        )));
    }

    @Test
    void secondPageOfNews() throws Exception {
        when(newsService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoNews());

        mvc.perform(get(samples.baseUrl()).param("page", "1").param("size", "7"))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(newsService).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(1),
                () -> assertThat(p.getPageSize()).isEqualTo(7)
        )));
    }

    @Test
    void findByValidId() throws Exception {
        Mockito.when(newsService.findById(Mockito.eq(samples.validId()))).thenReturn(samples.greatNews());

        mvc.perform(get(samples.validIdUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.updateResponse(), true)
                );
    }

    @Test
    void findingByInvalidIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.eq(samples.invalidId())))
                .thenThrow(new NewsNotFoundException(samples.invalidId()));

        mvc.perform(get(samples.invalidIdUrl()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("News"))
                );
    }

    @Test
    void addNews() throws Exception {
        News expected = samples.sadNews();
        Mockito.when(newsService.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(post(samples.baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.insertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.insertResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> AssertionHelpers.assertIdIsNull(actual),
                () -> AssertionHelpers.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void addingWithInvalidAuthorIdFails() throws Exception {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(post(samples.baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void addingWithInvalidCategoryIdFails() throws Exception {
        Mockito.when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(1L));

        mvc.perform(put(samples.validIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }

    @Test
    void updateNews() throws Exception {
        News current = samples.sadNews();
        Mockito.when(newsService.findById(Mockito.eq(samples.validId()))).thenReturn(current);

        News expected = samples.greatNews();
        Mockito.when(newsService.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(put(samples.validIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.updateResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> AssertionHelpers.assertHaveSameIds(current, actual),
                () -> AssertionHelpers.assertHaveSameAuditDates(current, actual),
                () -> AssertionHelpers.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void updatingWithInvalidNewsIdCreatesNewEntity() throws Exception {
        Mockito.when(newsService.findById(Mockito.eq(samples.invalidId())))
                .thenThrow(new NewsNotFoundException(samples.invalidId()));
        News expected = samples.sadNews();
        Mockito.when(newsService.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(put(samples.invalidIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.insertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.insertResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> AssertionHelpers.assertIdIsNull(actual),
                () -> AssertionHelpers.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void updatingWithInvalidAuthorIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong())).thenReturn(samples.sadNews());
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(put(samples.validIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void updatingWithInvalidCategoryIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong())).thenReturn(samples.sadNews());
        Mockito.when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(1L));

        mvc.perform(put(samples.validIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(newsService.findById(Mockito.eq(samples.validId()))).thenReturn(samples.greatNews());

        mvc.perform(delete(samples.validIdUrl()))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(newsService).delete(Mockito.assertArg(n ->
                assertThat(n).hasFieldOrPropertyWithValue("id", samples.validId())));
    }

    @Test
    void deletingByInvalidIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong())).thenThrow(NewsNotFoundException.class);

        mvc.perform(delete(samples.invalidIdUrl()))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.AuthorNotFoundException;
import io.github.hrashk.news.api.categories.CategoryNotFoundException;
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

@Import({NewsJsonSamples.class})
class NewsControllerTest extends ControllerTest {
    @Autowired
    private NewsJsonSamples json;
    
    public String newsUrl() {
        return "/api/v1/news";
    }
    
    public String newsUrl(Long id) {
        return newsUrl() + "/" + id;
    }

    @Test
    void firstPageOfNews() throws Exception {
        when(newsService.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoNews());

        mvc.perform(get(newsUrl()))
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

        mvc.perform(get(newsUrl()).param("page", "1").param("size", "7"))
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
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenReturn(samples.greatNews());

        mvc.perform(get(newsUrl(7L)))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.updateResponse(), true)
                );
    }

    @Test
    void findingByInvalidIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenThrow(new NewsNotFoundException(1L));

        mvc.perform(get(newsUrl(-1L)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("News"))
                );
    }

    @Test
    void addNews() throws Exception {
        mvc.perform(post(newsUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.insertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.insertResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(n ->
                assertThat(n).hasFieldOrPropertyWithValue("id",null)
        ));
    }

    @Test
    void addingWithInvalidAuthorIdFails() throws Exception {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(post(newsUrl())
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

        mvc.perform(put(newsUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }

    @Test
    void updateNews() throws Exception {
        Mockito.when(newsService.addOrReplace(Mockito.any(News.class)))
                .thenReturn(samples.greatNews());

        mvc.perform(put(newsUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().json(json.updateResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(n ->
                assertThat(n).hasFieldOrPropertyWithValue("id",7L)
        ));
    }

    @Test
    void updateMissingNews() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenThrow(new NewsNotFoundException(1L));

        mvc.perform(put(newsUrl(-1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.insertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().json(json.insertResponse(), true)
                );

        Mockito.verify(newsService).addOrReplace(Mockito.assertArg(n ->
                assertThat(n).hasFieldOrPropertyWithValue("id",null)
        ));
    }

    @Test
    void updatingWithInvalidAuthorIdFails() throws Exception {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenThrow(new AuthorNotFoundException(1L));

        mvc.perform(put(newsUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Author"))
                );
    }

    @Test
    void updatingWithInvalidCategoryIdFails() throws Exception {
        Mockito.when(categoryService.findById(Mockito.anyLong()))
                .thenThrow(new CategoryNotFoundException(1L));

        mvc.perform(put(newsUrl(7L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.updateRequest()))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("message").value(containsString("Category"))
                );
    }

    @Test
    void deleteByValidId() throws Exception {
        mvc.perform(delete(newsUrl(7L)))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(newsService).delete(Mockito.assertArg(n ->
                assertThat(n).hasFieldOrPropertyWithValue("id", 7L)));
    }

    @Test
    void deletingByInvalidIdFails() throws Exception {
        Mockito.when(newsService.findById(Mockito.anyLong())).thenThrow(NewsNotFoundException.class);

        mvc.perform(delete(newsUrl(-1L)))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

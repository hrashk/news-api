package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.news.web.NewsController;
import io.github.hrashk.news.api.news.web.NewsMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
class NewsControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private NewsJsonSamples json;
    @Autowired
    private NewsSamples samples;
    @MockBean
    private NewsService service;

    @TestConfiguration
    @Import({NewsSamples.class, NewsJsonSamples.class})
    static class AppConfig {
        @Bean
        NewsMapper mapper() {
            return Mappers.getMapper(NewsMapper.class);
        }
    }

    @Test
    void firstPageOfNews() throws Exception {
        when(service.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoNews());

        mvc.perform(get(samples.baseUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(service).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(0),
                () -> assertThat(p.getPageSize()).isEqualTo(10)
        )));
    }

    @Test
    void secondPageOfNews() throws Exception {
        when(service.findAll(Mockito.any(Pageable.class))).thenReturn(samples.twoNews());

        mvc.perform(get(samples.baseUrl()).param("page", "1").param("size", "7"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.findAllResponse(), true)
                );

        Mockito.verify(service).findAll(Mockito.assertArg(p -> Assertions.assertAll(
                () -> assertThat(p.getPageNumber()).isEqualTo(1),
                () -> assertThat(p.getPageSize()).isEqualTo(7)
        )));
    }

    @Test
    void findByValidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(samples.validId()))).thenReturn(samples.greatNews());

        mvc.perform(get(samples.validIdUrl()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );
    }

    @Test
    void findByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(samples.invalidId()))).thenThrow(NoSuchElementException.class);

        mvc.perform(get(samples.invalidIdUrl()))
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void addNews() throws Exception {
        News expected = samples.greatNews();
        Mockito.when(service.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(post(samples.baseUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> samples.assertIdIsNull(actual),
                () -> samples.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void updateByValidId() throws Exception {
        News current = samples.sadNews();
        Mockito.when(service.findById(Mockito.eq(samples.validId()))).thenReturn(current);

        News expected = samples.greatNews();
        Mockito.when(service.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(put(samples.validIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> samples.assertHaveSameIds(current, actual),
                () -> samples.assertHaveSameAuditDates(current, actual),
                () -> samples.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void updateByInvalidId() throws Exception {
        Mockito.when(service.findById(Mockito.eq(samples.invalidId()))).thenThrow(NoSuchElementException.class);
        News expected = samples.greatNews();
        Mockito.when(service.addOrReplace(Mockito.any(News.class))).thenReturn(expected);

        mvc.perform(put(samples.invalidIdUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.upsertRequest()))
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(json.upsertResponse(), true)
                );

        Mockito.verify(service).addOrReplace(Mockito.assertArg(actual -> Assertions.assertAll(
                () -> samples.assertIdIsNull(actual),
                () -> samples.assertAreSimilar(expected, actual)
        )));
    }

    @Test
    void deleteByValidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(true);

        mvc.perform(delete(samples.validIdUrl()))
                .andExpectAll(
                        status().isNoContent()
                );

        Mockito.verify(service).removeById(Mockito.eq(samples.validId()));
    }

    @Test
    void deleteByInvalidId() throws Exception {
        Mockito.when(service.contains(Mockito.anyLong())).thenReturn(false);

        mvc.perform(delete(samples.invalidIdUrl()))
                .andExpectAll(
                        status().isNotFound()
                );
    }
}

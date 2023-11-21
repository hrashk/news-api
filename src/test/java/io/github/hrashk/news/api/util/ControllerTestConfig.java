package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.web.AuthorMapper;
import io.github.hrashk.news.api.categories.web.CategoryMapper;
import io.github.hrashk.news.api.news.web.NewsMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(EntitySamples.class)
public class ControllerTestConfig {
    @Bean
    NewsMapper newsMapper() {
        return Mappers.getMapper(NewsMapper.class);
    }

    @Bean
    AuthorMapper authorMapper() {
        return Mappers.getMapper(AuthorMapper.class);
    }

    @Bean
    CategoryMapper categoryMapper() {
        return Mappers.getMapper(CategoryMapper.class);
    }
}

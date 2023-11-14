package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.authors.web.AuthorMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({AuthorJsonSamples.class, AuthorSamples.class})
class AuthorConfig {
    @Bean
    AuthorMapper mapper() {
        return Mappers.getMapper(AuthorMapper.class);
    }
}

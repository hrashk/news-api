package io.github.hrashk.news.api.authors;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@TestComponent
@Getter
@Accessors(fluent = true)
public class AuthorJsonSamples {
    private String findAllResponse;

    private String upsertResponse;

    private String upsertRequest;

    @Value("classpath:authors/find_all_response.json")
    void setFindAllResponse(Resource r) throws IOException {
        findAllResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:authors/upsert_response.json")
    void setUpsertResponse(Resource r) throws IOException {
        upsertResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:authors/upsert_request.json")
    void setUpsertRequest(Resource r) throws IOException {
        upsertRequest = r.getContentAsString(StandardCharsets.UTF_8);
    }
}

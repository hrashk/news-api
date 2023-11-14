package io.github.hrashk.news.api.news;

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
public class NewsJsonSamples {
    private String findAllResponse;

    private String upsertResponse;

    private String upsertRequest;

    @Value("classpath:news/find_all_response.json")
    void readFindAllResponse(Resource r) throws IOException {
        findAllResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:news/upsert_response.json")
    void readUpsertResponse(Resource r) throws IOException {
        upsertResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:news/upsert_request.json")
    void readUpsertRequest(Resource r) throws IOException {
        upsertRequest = r.getContentAsString(StandardCharsets.UTF_8);
    }
}

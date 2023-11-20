package io.github.hrashk.news.api.comments;

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
public class CommentJsonSamples {
    private String upsertResponse;

    private String upsertRequest;

    @Value("classpath:comments/upsert_response.json")
    void readUpsertResponse(Resource r) throws IOException {
        upsertResponse = r.getContentAsString(StandardCharsets.UTF_8);
    }

    @Value("classpath:comments/upsert_request.json")
    void readUpsertRequest(Resource r) throws IOException {
        upsertRequest = r.getContentAsString(StandardCharsets.UTF_8);
    }
}

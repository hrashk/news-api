package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Builder
@RequiredArgsConstructor
public class HttpExecutable implements Executable {
    final HttpMethod method;
    final String url;
    final Author authn;
    final Object body;
    final HttpStatus expectedStatus;
    final TestRestTemplate rest;
    @Singular
    final List<Object> urlVariables;

    @Override
    public void execute() {
        ResponseEntity<?> response = template()
                .exchange(url, method, httpEntity(), Map.class, urlVariables.toArray());

        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    }

    private HttpEntity<?> httpEntity() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return body == null ? HttpEntity.EMPTY : new HttpEntity<>(body, headers);
    }

    private TestRestTemplate template() {
        return authn == null ? rest : rest.withBasicAuth(authn.getUsername(), authn.getPassword());
    }
}

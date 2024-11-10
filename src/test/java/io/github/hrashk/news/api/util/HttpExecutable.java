package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpExecutable implements Executable {
    final HttpMethod method;
    final String url;
    final Author authn;
    final Object body;
    final HttpStatus expectedStatus;
    final Object[] urlVariables;
    final TestRestTemplate rest;

    public HttpExecutable(HttpMethod method, String url, Author authn, Object body, HttpStatus expectedStatus, TestRestTemplate rest, Object... urlVariables) {
        this.url = url;
        this.authn = authn;
        this.expectedStatus = expectedStatus;
        this.urlVariables = urlVariables;
        this.body = body;
        this.method = method;
        this.rest = rest;
    }

    @Override
    public void execute() {
        ResponseEntity<?> response = request().exchange(url, method, entity(), Map.class, urlVariables);

        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    }

    private HttpEntity<?> entity() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return body == null ? HttpEntity.EMPTY : new HttpEntity<>(body, headers);
    }

    private TestRestTemplate request() {
        return authn == null ? rest : rest.withBasicAuth(authn.getUsername(), authn.getPassword());
    }
}

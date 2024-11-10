package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class AuthorizationAuthorTest extends ControllerTest {
    @TestFactory
    public List<DynamicTest> findById() {
        Long plainUserId = seeder.plainUser().getId();
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        return List.of(
                findById("as admin -> ok", seeder.admin(), HttpStatus.OK, plainUserId),
                findById("as moderator -> ok", seeder.moderator(), HttpStatus.OK, plainUserId),
                findById("as user -> ok", seeder.plainUser(), HttpStatus.OK, plainUserId),
                findById("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, plainUserId),
                findById("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, plainUserId),
                findById("wrong creds -> unauthorized",
                        authorNotInSystem, HttpStatus.UNAUTHORIZED, plainUserId),
                findById("another author as user -> forbidden",
                        seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.admin().getId())
        );
    }

    private DynamicTest findById(String message, Author authn, HttpStatus status, Long id) {
        return dynamicTest(message,
                new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_ID_URL, authn, null, status, id));
    }

    @TestFactory
    public List<DynamicTest> findAll() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        return List.of(
                findAll("as admin -> ok", seeder.admin(), HttpStatus.OK),
                findAll("as moderator -> forbidden", seeder.moderator(), HttpStatus.FORBIDDEN),
                findAll("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN),
                findAll("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN),
                findAll("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED),
                findAll("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED)
        );
    }

    private DynamicTest findAll(String message, Author authn, HttpStatus status) {
        return dynamicTest(message,
                new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_URL, authn, null, status));
    }

    @TestFactory
    public List<DynamicTest> add() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        return List.of(
                add("as admin -> created", seeder.admin(), HttpStatus.CREATED, request),
                add("as moderator -> created", seeder.moderator(), HttpStatus.CREATED, request),
                add("as user -> forbidden", seeder.plainUser(), HttpStatus.FORBIDDEN, request),
                add("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request),
                add("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request),
                add("wrong creds -> unauthorized", authorNotInSystem, HttpStatus.UNAUTHORIZED, request)
        );
    }

    private DynamicTest add(String message, Author authn, HttpStatus status, UpsertAuthorRequest body) {
        return dynamicTest(message, new HttpExecutable(HttpMethod.POST, Constants.AUTHORS_URL, authn, body, status));
    }

    class HttpExecutable implements Executable {
        final HttpMethod method;
        final String url;
        final Author authn;
        final Object body;
        final HttpStatus expectedStatus;
        final Object[] urlVariables;

        HttpExecutable(HttpMethod method, String url, Author authn, Object body, HttpStatus expectedStatus, Object... urlVariables) {
            this.url = url;
            this.authn = authn;
            this.expectedStatus = expectedStatus;
            this.urlVariables = urlVariables;
            this.body = body;
            this.method = method;
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
}

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
                dynamicTest("as admin -> ok",
                        findById(seeder.admin(), HttpStatus.OK, plainUserId)),
                dynamicTest("as moderator -> ok",
                        findById(seeder.moderator(), HttpStatus.OK, plainUserId)),
                dynamicTest("as user -> ok",
                        findById(seeder.plainUser(), HttpStatus.OK, plainUserId)),
                dynamicTest("no roles -> forbidden",
                        findById(seeder.withoutRoles(), HttpStatus.FORBIDDEN, plainUserId)),
                dynamicTest("anonymous -> unauthorized",
                        findById(null, HttpStatus.UNAUTHORIZED, plainUserId)),
                dynamicTest("wrong creds -> unauthorized",
                        findById(authorNotInSystem, HttpStatus.UNAUTHORIZED, plainUserId)),
                dynamicTest("another author as user -> forbidden",
                        findById(seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.admin().getId()))
        );
    }

    private HttpExecutable findById(Author authn, HttpStatus status, Long id) {
        return new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_ID_URL, authn, null, status, id);
    }

    @TestFactory
    public List<DynamicTest> findAll() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        return List.of(
                dynamicTest("as admin -> ok",
                        findAll(seeder.admin(), HttpStatus.OK)),
                dynamicTest("as moderator -> forbidden",
                        findAll(seeder.moderator(), HttpStatus.FORBIDDEN)),
                dynamicTest("as user -> forbidden",
                        findAll(seeder.plainUser(), HttpStatus.FORBIDDEN)),
                dynamicTest("no roles -> forbidden",
                        findAll(seeder.withoutRoles(), HttpStatus.FORBIDDEN)),
                dynamicTest("anonymous -> unauthorized",
                        findAll(null, HttpStatus.UNAUTHORIZED)),
                dynamicTest("wrong creds -> unauthorized",
                        findAll(authorNotInSystem, HttpStatus.UNAUTHORIZED))
        );
    }

    private HttpExecutable findAll(Author authn, HttpStatus status) {
        return new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_URL, authn, null, status);
    }

    @TestFactory
    public List<DynamicTest> add() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();
        UpsertAuthorRequest request = new UpsertAuthorRequest(
                "lorem", "ipsum", "random", "password");

        return List.of(
                dynamicTest("as admin -> created",
                        add(seeder.admin(), HttpStatus.CREATED, request)),
                dynamicTest("as moderator -> created",
                        add(seeder.moderator(), HttpStatus.CREATED, request)),
                dynamicTest("as user -> forbidden",
                        add(seeder.plainUser(), HttpStatus.FORBIDDEN, request)),
                dynamicTest("no roles -> forbidden",
                        add(seeder.withoutRoles(), HttpStatus.FORBIDDEN, request)),
                dynamicTest("anonymous -> unauthorized",
                        add(null, HttpStatus.UNAUTHORIZED, request)),
                dynamicTest("wrong creds -> unauthorized",
                        add(authorNotInSystem, HttpStatus.UNAUTHORIZED, request))
        );
    }

    private HttpExecutable add(Author authn, HttpStatus status, Object body) {
        return new HttpExecutable(HttpMethod.POST, Constants.AUTHORS_URL, authn, body, status);
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

package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.util.ControllerTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
                dynamicTest("another author as user -> unauthorized",
                        findById(seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.admin().getId()))
        );
    }

    private DynamicAuthorTest findById(Author authn, HttpStatus status, Long id) {
        return new DynamicAuthorTest(Constants.AUTHORS_ID_URL, authn, status, id);
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
                dynamicTest("no roles -> unauthorized",
                        findAll(seeder.withoutRoles(), HttpStatus.FORBIDDEN)),
                dynamicTest("anonymous -> unauthorized",
                        findAll(null, HttpStatus.UNAUTHORIZED)),
                dynamicTest("wrong creds -> unauthorized",
                        findAll(authorNotInSystem, HttpStatus.UNAUTHORIZED))
        );
    }

    private DynamicAuthorTest findAll(Author authn, HttpStatus status) {
        return new DynamicAuthorTest(Constants.AUTHORS_URL, authn, status);
    }

    class DynamicAuthorTest implements Executable {
        final Author authn;
        protected Object[] urlVariables;
        protected HttpStatus status;
        protected String url;

        DynamicAuthorTest(String url, Author authn, HttpStatus status, Object... urlVariables) {
            this.url = url;
            this.authn = authn;
            this.status = status;
            this.urlVariables = urlVariables;
        }

        @Override
        public void execute() {
            ResponseEntity<?> response = request().getForEntity(url, Map.class, urlVariables);

            assertThat(response.getStatusCode()).isEqualTo(status);
        }

        private TestRestTemplate request() {
            return authn == null ? rest : rest.withBasicAuth(authn.getUsername(), authn.getPassword());
        }
    }
}

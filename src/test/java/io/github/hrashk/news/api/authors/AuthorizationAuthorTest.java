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
                dynamicTest("as admin", findById(seeder.admin(), HttpStatus.OK, plainUserId)),
                dynamicTest("as moderator", findById(seeder.moderator(), HttpStatus.OK, plainUserId)),
                dynamicTest("as user", findById(seeder.plainUser(), HttpStatus.OK, plainUserId)),
                dynamicTest("no roles", findById(seeder.withoutRoles(), HttpStatus.FORBIDDEN, plainUserId)),
                dynamicTest("anonymous", findById(null, HttpStatus.UNAUTHORIZED, plainUserId)),
                dynamicTest("wrong creds", findById(authorNotInSystem, HttpStatus.UNAUTHORIZED, plainUserId)),
                dynamicTest("user cannot find another author",
                        findById(seeder.plainUser(), HttpStatus.FORBIDDEN, seeder.admin().getId())),
                dynamicTest("cannot find invalid author",
                        findById(seeder.admin(), HttpStatus.NOT_FOUND, INVALID_ID))
        );
    }

    private DynamicAuthorTest findById(Author authn, HttpStatus status, Long query) {
        return new DynamicAuthorTest(Constants.AUTHORS_ID_URL, authn, status, query);
    }

    class DynamicAuthorTest implements Executable {
        final Author authn;
        protected Long query;
        protected HttpStatus status;
        protected String url;

        DynamicAuthorTest(String url, Author authn, HttpStatus status, Long authorId) {
            this.authn = authn;
            this.query = authorId;
            this.status = status;
            this.url = url;
        }

        @Override
        public void execute() {
            ResponseEntity<?> response = request().getForEntity(url, Map.class, query);

            assertThat(response.getStatusCode()).isEqualTo(status);
        }

        private TestRestTemplate request() {
            return authn == null ? rest : rest.withBasicAuth(authn.getUsername(), authn.getPassword());
        }
    }
}

package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.Constants;
import io.github.hrashk.news.api.authors.web.UpsertAuthorRequest;
import io.github.hrashk.news.api.util.ControllerTest;
import io.github.hrashk.news.api.util.HttpExecutable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.List;

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
                new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_ID_URL, authn, null, status, rest, id));
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
                new HttpExecutable(HttpMethod.GET, Constants.AUTHORS_URL, authn, null, status, rest));
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
        return dynamicTest(message,
                new HttpExecutable(HttpMethod.POST, Constants.AUTHORS_URL, authn, body, status, rest));
    }

    @TestFactory
    public List<DynamicTest> update() {
        Author authorNotInSystem = Author.builder().username("fake").password("author").build();

        Author user = seeder.plainUser();
        Long plainUserId = user.getId();
        var request = new UpsertAuthorRequest(
                "random", "last name", user.getUsername(), user.getPassword());

        return List.of(
                update("as admin -> ok", seeder.admin(), HttpStatus.OK, request, plainUserId),
                update("as moderator -> ok", seeder.moderator(), HttpStatus.OK, request, plainUserId),
                update("as user -> ok", user, HttpStatus.OK, request, plainUserId),
                update("no roles -> forbidden", seeder.withoutRoles(), HttpStatus.FORBIDDEN, request, plainUserId),
                update("anonymous -> unauthorized", null, HttpStatus.UNAUTHORIZED, request, plainUserId),
                update("wrong creds -> unauthorized",
                        authorNotInSystem, HttpStatus.UNAUTHORIZED, request, plainUserId),
                update("another author as user -> forbidden",
                        user, HttpStatus.FORBIDDEN, request, seeder.admin().getId())
        );
    }

    private DynamicTest update(String message, Author authn, HttpStatus status, UpsertAuthorRequest body, Long id) {
        return dynamicTest(message,
                new HttpExecutable(HttpMethod.PUT, Constants.AUTHORS_ID_URL, authn, body, status, rest, id));
    }
}

package io.github.hrashk.news.api.aspects;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.exceptions.InvalidUserException;
import io.github.hrashk.news.api.news.NewsService;
import io.github.hrashk.news.api.security.AppUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.function.Function;

@Aspect
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final HttpServletRequest request;

    @Before("@annotation(SameAuthorLenient) && target(service)")
    public void checkNews(JoinPoint jp, NewsService service) {
        checkUser(id -> service.findById(id).getAuthor().getId(), false);
    }

    @Before("@annotation(SameAuthorLenient) && target(service)")
    public void checkComment(JoinPoint jp, CommentService service) {
        checkUser(id -> service.findById(id).getAuthor().getId(), false);
    }

    @Before("@annotation(SameAuthorStrict) && target(service)")
    public void checkNewsStrict(JoinPoint jp, NewsService service) {
        checkUser(id -> service.findById(id).getAuthor().getId(), true);
    }

    @Before("@annotation(SameAuthorStrict) && target(service)")
    public void checkCommentStrict(JoinPoint jp, CommentService service) {
        checkUser(id -> service.findById(id).getAuthor().getId(), true);
    }

    @Before("target(service) && execution(* *.findById(Long)) && args(id)")
    public void checkAuthorFindById(JoinPoint jp, AuthorService service, Long id) {
        checkAdminOrSameAuthor(id);
    }

    @Before("target(service) && execution(* *.update(Long, ..)) && args(id, ..)")
    public void checkAuthorUpdate(JoinPoint jp, AuthorService service, Long id) {
        checkAdminOrSameAuthor(id);
    }

    @Before("target(service) && execution(* *.deleteById(Long)) && args(id)")
    public void checkAuthorDelete(JoinPoint jp, AuthorService service, Long id) {
        checkAdminOrSameAuthor(id);
    }

    private void checkUser(Function<Long, Long> authorIdLookup, boolean strict) {
        Long authorId;

        try {
            Long entityId = getIdFromPath();
            authorId = authorIdLookup.apply(entityId);
        } catch (NumberFormatException | EntityNotFoundException ex) {
            return; // when the web controller searches for an entity by id right after creating it
        }

        Author author = getAuthorFromPrincipal();

        if ((strict || author.hasOnlyUserRole()) && !author.getId().equals(authorId))
            throw new InvalidUserException();
    }

    private void checkAdminOrSameAuthor(Long authorId) {
        Author author = getAuthorFromPrincipal();

        if (author.hasOnlyUserRole() && !author.getId().equals(authorId))
            throw new InvalidUserException();
    }

    private Long getIdFromPath() {
        var variables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        return Long.parseLong(String.valueOf(variables.get("id")));
    }

    private Author getAuthorFromPrincipal() {
        Authentication authn = (Authentication) request.getUserPrincipal();
        AppUserPrincipal principal = (AppUserPrincipal) authn.getPrincipal();

        return principal.getAuthor();
    }
}

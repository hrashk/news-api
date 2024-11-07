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

    @Before("@annotation(io.github.hrashk.news.api.aspects.SameAuthorLenient) && target(service)")
    public void checkNews(JoinPoint jp, NewsService service) {
        checkUser(id -> service.findById(id).getAuthor().getId());
    }

    @Before("@annotation(io.github.hrashk.news.api.aspects.SameAuthorLenient) && target(service)")
    public void checkComment(JoinPoint jp, CommentService service) {
        checkUser(id -> service.findById(id).getAuthor().getId());
    }

    @Before("@annotation(io.github.hrashk.news.api.aspects.SameAuthorLenient) && target(service)")
    public void checkAuthor(JoinPoint jp, AuthorService service) {
        checkUser(id -> id);
    }

    private void checkUser(Function<Long, Long> authorIdLookup) {
        Long authorId;

        try {
            Long entityId = getIdFromPath();
            authorId = authorIdLookup.apply(entityId);
        } catch (NumberFormatException | EntityNotFoundException ex) {
            return; // when the web controller searches for an entity by id right after creating it
        }

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

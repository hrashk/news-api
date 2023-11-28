package io.github.hrashk.news.api.aspects;

import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.exceptions.InvalidUserException;
import io.github.hrashk.news.api.news.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.function.Function;

@Aspect
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final HttpServletRequest request;

    @Before("@annotation(SameAuthor) && target(service)")
    public void checkNews(JoinPoint jp, NewsService service) {
        checkUser(id -> service.findById(id).getAuthor().getId());
    }

    @Before("@annotation(SameAuthor) && target(service)")
    public void checkComment(JoinPoint jp, CommentService service) {
        checkUser(id -> service.findById(id).getAuthor().getId());
    }

    private void checkUser(Function<Long, Long> authorIdLookup) {
        Long authorId;

        try {
            Long entityId = getIdFromPath();
            authorId = authorIdLookup.apply(entityId);
        }
        catch (NumberFormatException | EntityNotFoundException ex) {
            return; // skip if no id in the url path or no entity matches the id
        }

        Long userId = getUserIdParam();

        if (!userId.equals(authorId))
            throw new InvalidUserException();
    }

    private Long getIdFromPath() {
        var variables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        return Long.parseLong(String.valueOf(variables.get("id")));
    }

    private long getUserIdParam() {
        try {
            return Long.parseLong(request.getParameter("userId"));
        } catch (NumberFormatException ex) {
            throw new InvalidUserException(ex);
        }
    }
}

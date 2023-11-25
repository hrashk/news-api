package io.github.hrashk.news.api.aspects;

import io.github.hrashk.news.api.comments.CommentService;
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
public class UserValidation {
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
        Long id = getIdFromPath();

        if (id == null) // skip the add operation
            return;

        Long userId = getUserIdParam();

        Long authorId = authorIdLookup.apply(id);

        if (!userId.equals(authorId))
            throw new InvalidUserException("Only the author of the news can alter it");
    }

    private Long getIdFromPath() {
        Object urlVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (urlVariables instanceof Map<?, ?> variables && variables.get("id") != null) {
            return Long.parseLong(String.valueOf(variables.get("id")));
        } else
            return null;
    }

    private long getUserIdParam() {
        try {
            return Long.parseLong(request.getParameter("userId"));
        } catch (NumberFormatException ex) {
            throw new InvalidUserException("A valid userId must be specified", ex);
        }
    }
}

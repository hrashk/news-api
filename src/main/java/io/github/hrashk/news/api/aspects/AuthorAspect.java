package io.github.hrashk.news.api.aspects;

import io.github.hrashk.news.api.exceptions.InvalidUserException;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorAspect {
    private final HttpServletRequest request;

    @Before("@annotation(SameAuthor) && target(service)")
    public void checkNewsAuthor(JoinPoint jp, NewsService service) {
        Long id = getIdFromPath();

        if (id == null) // skip the add operation
            return;

        Long userId = getUserIdParam();

        News news = service.findById(id);
        Long authorId = news.getAuthor().getId();

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

    @Before("@annotation(SameAuthor) && within(*..CommentService) && args(id,..)")
    public void checkCommentAuthor(JoinPoint jp, Long id) {
        System.out.printf("checking the author of comment %d in method %s%n", id, jp.getSignature().getName());
    }
}

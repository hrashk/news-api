package io.github.hrashk.news.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NewsApiAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    ErrorInfo handleMissingEntity(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    ErrorInfo handleValidationIssues(HttpServletRequest req, Exception ex) {
        return new ErrorInfo(ex.getMessage());
    }
}

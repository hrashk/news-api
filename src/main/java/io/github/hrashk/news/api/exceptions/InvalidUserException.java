package io.github.hrashk.news.api.exceptions;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() {
        this(null);
    }

    public InvalidUserException(Throwable cause) {
        super("You are not allowed to make this change", cause);
    }
}

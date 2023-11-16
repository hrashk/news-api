package io.github.hrashk.news.api.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super("%s with id %d is not found".formatted(entityType, id));
    }
}

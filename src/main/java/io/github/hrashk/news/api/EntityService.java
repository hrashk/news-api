package io.github.hrashk.news.api;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;

public interface EntityService<T, ID> {
    T findById(ID id) throws EntityNotFoundException;

    void replaceById(ID id, T entity) throws EntityNotFoundException;

    ID add(T entity);

    void deleteById(ID id) throws EntityNotFoundException;
}

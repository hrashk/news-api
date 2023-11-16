package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;

public class CategoryNotFoundException extends EntityNotFoundException {
    public CategoryNotFoundException(Long id) {
        super("Category", id);
    }
}

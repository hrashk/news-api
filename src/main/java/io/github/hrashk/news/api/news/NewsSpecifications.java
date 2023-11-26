package io.github.hrashk.news.api.news;

import org.springframework.data.jpa.domain.Specification;

public final class NewsSpecifications {
    public static Specification<News> hasCategory(Long id) {
        return (root, query, builder) -> id == null ? null : builder.equal(root.join("category").get("id"), id);
    }

    public static Specification<News> hasAuthor(Long id) {
        return (root, query, builder) -> id == null ? null : builder.equal(root.join("author").get("id"), id);
    }

    public static Specification<News> fromFilter(NewsFilter filter) {
        return Specification.where(hasAuthor(filter.getAuthorId()))
                .and(hasCategory(filter.getCategoryId()));
    }
}

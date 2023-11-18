package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByCategory(Category category);
}

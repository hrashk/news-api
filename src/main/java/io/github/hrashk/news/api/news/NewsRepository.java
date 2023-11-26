package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    boolean existsByCategory(Category category);
    boolean existsByCategoryId(Long id);

    @Override
    @EntityGraph(attributePaths = "comments")
    Page<News> findAll(Specification<News> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "comments")
    Optional<News> findById(Long aLong);
}

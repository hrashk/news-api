package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import(CategoryService.class)
class CategoryServiceTest {
    private static final long INVALID_ID = 11333L;

    @Autowired
    private CategoryService service;

    @Autowired
    private CategoryRepository repository;
    private List<Category> savedCategory;

    @BeforeEach
    public void injectCategory() {
        this.savedCategory = repository.saveAll(CategorySamples.twoCategories());
    }

    @Test
    void findAll() {
        assertThat(service.findAll()).isNotEmpty();
    }

    @Test
    void findByValidId() {
        Category news = savedCategory.get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(INVALID_ID))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveWithNullId() {
        Category saved = service.addOrReplace(CategorySamples.withoutId());

        assertThat(saved.getId()).as("Category id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = CategorySamples.withId();
        long originalid = n.getId(); // the news object is changed after saving

        Category saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("Category id").isNotEqualTo(originalid);
    }

    @Test
    void containsValidId() {
        Category first = savedCategory.get(0);

        assertThat(service.contains(first.getId())).isTrue();
    }

    @Test
    void doesNotContainInvalidId() {
        assertThat(service.contains(INVALID_ID)).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedCategory.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}
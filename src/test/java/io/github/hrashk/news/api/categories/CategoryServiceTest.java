package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.ContainerJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({CategoryService.class, CategorySamples.class})
class CategoryServiceTest {
    @Autowired
    private CategoryService service;
    @Autowired
    private CategorySamples samples;
    @Autowired
    private CategoryRepository repository;
    private List<Category> savedCategory;

    @BeforeEach
    public void injectCategory() {
        this.savedCategory = repository.saveAll(samples.twoCategories());
    }

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 1))).hasSize(1);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 1))).hasSize(1);
    }

    @Test
    void findByValidId() {
        Category news = savedCategory.get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(samples.invalidId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveWithNullId() {
        Category saved = service.addOrReplace(samples.withoutId());

        assertThat(saved.getId()).as("Category id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = samples.withInvalidId();
        long originalId = n.getId(); // the news object is changed after saving

        Category saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("Category id").isNotEqualTo(originalId);
    }

    @Test
    void containsValidId() {
        Category first = savedCategory.get(0);

        assertThat(service.contains(first.getId())).isTrue();
    }

    @Test
    void doesNotContainInvalidId() {
        assertThat(service.contains(samples.invalidId())).isFalse();
    }

    @Test
    void removeById() {
        Long id = savedCategory.get(0).getId();

        service.removeById(id);

        assertThat(service.contains(id)).isFalse();
    }
}

package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.ContainerJpaTest;
import io.github.hrashk.news.api.seeder.DataSeeder;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContainerJpaTest
@Import({CategoryService.class, DataSeeder.class})
class CategoryServiceTest {
    @Autowired
    private CategoryService service;
    @Autowired
    private DataSeeder seeder;

    private List<Category> savedCategories;

    @BeforeEach
    public void seedCategories() {
        seeder.seed(5);
        savedCategories = seeder.categories();
    }

    @Test
    void firstPage() {
        assertThat(service.findAll(PageRequest.of(0, 3))).hasSize(3);
    }

    @Test
    void secondPage() {
        assertThat(service.findAll(PageRequest.of(1, 2))).hasSize(2);
    }

    @Test
    void findByValidId() {
        Category news = savedCategories.get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void saveWithNullId() {
        Category saved = service.addOrReplace(seeder.aRandomCategory(-1L));

        assertThat(saved.getId()).as("Category id").isNotNull();
    }

    @Test
    void saveWithNonNullId() {
        var n = seeder.aRandomCategory(-1L);
        n.setId(-1L);

        Category saved = service.addOrReplace(n);

        assertThat(saved.getId()).as("Category id").isGreaterThan(0L);
    }

    @Test
    void delete() {
        Category categoryWithoutNews = service.addOrReplace(seeder.aRandomCategory(-1L));
        Long id = categoryWithoutNews.getId();

        service.delete(categoryWithoutNews);

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void deletingWithNewsFails() {
        Category categoryWithNews = seeder.news().get(0).getCategory();

        assertThatThrownBy(() -> service.delete(categoryWithNews))
                .isInstanceOf(ValidationException.class);
    }
}

package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.news.NewsService;
import io.github.hrashk.news.api.util.ServiceTest;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import({CategoryService.class, NewsService.class})
class CategoryServiceTest extends ServiceTest {
    @Autowired
    private CategoryService service;

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
        Category news = seeder.categories().get(0);
        Long validId = news.getId();

        assertThat(service.findById(validId)).isEqualTo(news);
    }

    @Test
    void findByInvalidId() {
        assertThatThrownBy(() -> service.findById(-1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void add() {
        Long id = service.add(seeder.aRandomCategory(-1L));
        seeder.flush();

        assertThat(id).as("Category id").isNotNull();
    }

    @Test
    void update() {
        var category = seeder.categories().get(1);
        category.setName("asdf");

        service.updateOrAdd(category.getId(), category);
        seeder.flush();

        assertThat(service.findById(category.getId())).hasFieldOrPropertyWithValue("name", "asdf");
    }

    @Test
    void delete() {
        Long id = service.add(seeder.aRandomCategory(-1L));

        service.deleteById(id);
        seeder.flush();

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deletingWithNewsFails() {
        Category categoryWithNews = seeder.news().get(0).getCategory();

        assertThatThrownBy(() -> service.deleteById(categoryWithNews.getId()))
                .isInstanceOf(ValidationException.class);
    }
}

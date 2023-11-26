package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.common.BaseService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import io.github.hrashk.news.api.news.NewsService;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends BaseService<Category, CategoryRepository> {
    private final NewsService newsService;

    protected CategoryService(CategoryRepository repository, NewsService newsService) {
        super(repository, "Category");
        this.newsService = newsService;
    }

    public List<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        if (newsService.existsByCategoryId(id))
            throw new ValidationException("Cannot delete the category because it has related news");

       super.deleteById(id);
    }
}

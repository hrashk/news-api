package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.CrudService;
import io.github.hrashk.news.api.news.NewsRepository;
import io.github.hrashk.news.api.util.BeanCopyUtils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CrudService<Category, Long> {
    private final CategoryRepository repository;
    private final NewsRepository newsRepository;

    public List<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public Category findById(Long id) throws CategoryNotFoundException {
        return repository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public void replaceById(Long id, Category entity) throws CategoryNotFoundException {
        var current = findById(id);
        BeanCopyUtils.copyProperties(entity, current);

        repository.save(current);
    }

    @Override
    public Long add(Category entity) {
        var saved = repository.save(entity);

        return saved.getId();
    }

    @Override
    public void deleteById(Long id) throws CategoryNotFoundException {
        var category = findById(id);

        if (newsRepository.existsByCategory(category))
            throw new ValidationException("Cannot delete the category because it has related news");

        repository.delete(category);
    }
}

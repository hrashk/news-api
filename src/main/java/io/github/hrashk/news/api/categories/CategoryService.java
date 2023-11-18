package io.github.hrashk.news.api.categories;

import io.github.hrashk.news.api.news.NewsRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final NewsRepository newsRepository;

    public List<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public Category findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category addOrReplace(Category category) {
        return repository.save(category);
    }

    public void delete(Category category) {
        if (newsRepository.existsByCategory(category))
            throw new ValidationException("Cannot delete the category because it has related news");

        repository.delete(category);
    }
}

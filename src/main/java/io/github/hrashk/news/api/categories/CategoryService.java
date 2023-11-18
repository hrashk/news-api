package io.github.hrashk.news.api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

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
        repository.delete(category);
    }
}

package io.github.hrashk.news.api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    /**
     * @throws java.util.NoSuchElementException if id is not found
     */
    public Category findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Category addOrReplace(Category category) {
        return repository.save(category);
    }

    public boolean contains(Long id) {
        return repository.existsById(id);
    }

    public void removeById(Long id) {
        repository.deleteById(id);
    }
}

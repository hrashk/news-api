package io.github.hrashk.news.api.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;

    public List<Comment> findAll() {
        return repository.findAll();
    }

    /**
     * @throws java.util.NoSuchElementException if id is not found
     */
    public Comment findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Comment addOrReplace(Comment category) {
        return repository.save(category);
    }

    public boolean contains(Long id) {
        return repository.existsById(id);
    }

    public void removeById(Long id) {
        repository.deleteById(id);
    }
}

package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.CrudService;
import io.github.hrashk.news.api.aspects.SameAuthor;
import io.github.hrashk.news.api.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements CrudService<Comment, Long> {
    private final CommentRepository repository;

    public List<Comment> findAll() {
        return repository.findAll();
    }

    @Override
    public Comment findById(Long id) throws CommentNotFoundException {
        return repository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    @SameAuthor
    @Override
    public void replaceById(Long id, Comment entity) throws CommentNotFoundException {
        var current = findById(id);
        BeanCopyUtils.copyProperties(entity, current);

        repository.save(current);
    }

    @Override
    public Long add(Comment entity) {
        var saved = repository.save(entity);

        return saved.getId();
    }

    @SameAuthor
    @Override
    public void deleteById(Long id) throws CommentNotFoundException {
        var comment = findById(id);

        repository.delete(comment);
    }
}

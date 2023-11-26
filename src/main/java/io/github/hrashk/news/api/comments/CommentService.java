package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.aspects.SameAuthor;
import io.github.hrashk.news.api.common.BaseService;
import io.github.hrashk.news.api.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService extends BaseService<Comment, CommentRepository> {
    protected CommentService(CommentRepository repository) {
        super(repository, "Comment");
    }

    public List<Comment> findAll() {
        return repository.findAll();
    }

    @SameAuthor
    @Override
    public Long updateOrAdd(Long id, Comment entity) {
        return super.updateOrAdd(id, entity);
    }

    @SameAuthor
    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        super.deleteById(id);
    }
}

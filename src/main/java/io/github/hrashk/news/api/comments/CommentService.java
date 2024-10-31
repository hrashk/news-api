package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.common.BaseService;
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
}

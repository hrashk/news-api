package io.github.hrashk.news.api.comments;

import io.github.hrashk.news.api.aspects.SameAuthor;
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

    public Comment findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    @SameAuthor
    public Comment addOrReplace(Comment comment) {
        return repository.save(comment);
    }

    @SameAuthor
    public void delete(Comment comment) {
        repository.delete(comment);
    }
}

package io.github.hrashk.news.api.comments.web;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentNotFoundException;
import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.news.web.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;
    private final NewsMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        Comment comment = mapper.mapToComment(id);

        return ResponseEntity.ok(mapper.map(comment));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody UpsertCommentRequest request) {
        Comment comment = mapper.map(request);
        Comment saved = service.addOrReplace(comment);

        CommentResponse response = mapper.map(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody UpsertCommentRequest request) {
        try {
            Comment comment = mapper.mapToComment(id);
            Comment requested = mapper.map(request);
            BeanUtils.copyProperties(requested, comment);

            Comment saved = service.addOrReplace(comment);

            return ResponseEntity.ok(mapper.map(saved));
        } catch (CommentNotFoundException ex) {
            return addComment(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Comment comment = mapper.mapToComment(id);

        service.delete(comment);

        return ResponseEntity.noContent().build();
    }
}

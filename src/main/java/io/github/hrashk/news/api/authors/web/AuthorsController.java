package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorsController {

    private final AuthorService service;
    private final AuthorsMapper mapper;

    @GetMapping
    public ResponseEntity<AuthorListResponse> getAllAuthors() {
        List<Author> authors = service.findAll();

        return ResponseEntity.ok(mapper.toResponse(authors));
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> addAuthor(@RequestBody UpsertAuthorRequest authorRequest) {
        Author author = mapper.toAuthor(authorRequest);
        Author saved = service.addOrReplace(author);

        return created(mapper.toResponse(saved));
    }

    private static ResponseEntity<AuthorResponse> created(AuthorResponse response) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        try {
            Author a = service.findById(id);

            return ResponseEntity.ok(mapper.toResponse(a));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @RequestBody UpsertAuthorRequest request) {
        try {
            Author author = service.findById(id);
            BeanUtils.copyProperties(request, author);

            Author saved = service.addOrReplace(author);

            return ResponseEntity.ok(mapper.toResponse(saved));
        } catch (NoSuchElementException ex) {
            return addAuthor(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (service.contains(id)) {
            service.removeById(id);

            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }
}

package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorsController {

    private final AuthorService service;

    @GetMapping
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        List<Author> authors = service.findAll();
        return ResponseEntity.ok(List.of(AuthorResponse.EMPTY, AuthorResponse.EMPTY));
    }

    @PostMapping
    public ResponseEntity<String> addAuthor() {
        return ResponseEntity.ok("Author added");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getAuthorById(@PathVariable String id) {
        return ResponseEntity.ok("Author " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAuthor(@PathVariable String id) {
        return ResponseEntity.ok("Author " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable String id) {
        return ResponseEntity.ok("Author " + id);
    }
}

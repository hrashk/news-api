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
    private final AuthorsMapper mapper;

    @GetMapping
    public ResponseEntity<AuthorListResponse> getAllAuthors() {
        List<Author> authors = service.findAll();
        return ResponseEntity.ok(new AuthorListResponse(mapper.toResponseList(authors)));
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

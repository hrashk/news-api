package io.github.hrashk.news.api.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
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
    public ResponseEntity<AuthorResponse> addAuthor(@RequestBody UpsertAuthorRequest authorRequest) {
        Author a = service.save(mapper.toAuthor(authorRequest));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.toResponse(a));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        Author a = service.findById(id);

        return ResponseEntity.ok(mapper.toResponse(a));
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

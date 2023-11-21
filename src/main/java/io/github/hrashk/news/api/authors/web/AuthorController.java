package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorNotFoundException;
import io.github.hrashk.news.api.authors.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/authors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;
    private final AuthorMapper mapper;

    @GetMapping
    public ResponseEntity<AuthorListResponse> getAllAuthors(@ParameterObject @PageableDefault Pageable pageable) {
        List<Author> authors = service.findAll(pageable);

        return ResponseEntity.ok(mapper.wrap(authors));
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> addAuthor(@RequestBody UpsertAuthorRequest authorRequest) {
        Author author = mapper.map(authorRequest);
        Author saved = service.addOrReplace(author);

        AuthorResponse response = mapper.map(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        Author a = mapper.map(id);

        return ResponseEntity.ok(mapper.map(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @RequestBody UpsertAuthorRequest request) {
        try {
            Author author = mapper.map(id);
            BeanUtils.copyProperties(request, author);

            Author saved = service.addOrReplace(author);

            return ResponseEntity.ok(mapper.map(saved));
        } catch (AuthorNotFoundException ex) {
            return addAuthor(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        Author author = mapper.map(id);

        service.delete(author);

        return ResponseEntity.noContent().build();
    }
}

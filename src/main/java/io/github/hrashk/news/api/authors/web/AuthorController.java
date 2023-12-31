package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        Author a = mapper.map(id);

        return ResponseEntity.ok(mapper.map(a));
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> addAuthor(@RequestBody @Valid UpsertAuthorRequest authorRequest) {
        Long id = service.add(mapper.map(authorRequest));

        AuthorResponse response = mapper.map(service.findById(id));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @RequestBody @Valid UpsertAuthorRequest authorRequest) {
        Long newId = service.updateOrAdd(id, mapper.map(authorRequest));

        AuthorResponse response = mapper.map(service.findById(newId));

        if (Objects.equals(newId, id))
            return ResponseEntity.ok(response);
        else
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

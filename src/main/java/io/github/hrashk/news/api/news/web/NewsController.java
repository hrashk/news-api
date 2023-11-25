package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsNotFoundException;
import io.github.hrashk.news.api.news.NewsService;
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

@RestController
@RequestMapping(value = "/api/v1/news", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;
    private final NewsMapper mapper;

    @GetMapping
    public ResponseEntity<NewsListResponse> getAllNews(
            @ParameterObject @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId) {
        List<News> news = service.findAll(pageable, authorId, categoryId);

        return ResponseEntity.ok(mapper.wrap(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        NewsResponse response = mapper.map(service.findById(id));

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<NewsResponse> addNews(@RequestBody @Valid UpsertNewsRequest authorRequest) {
        Long id = service.add(mapper.map(authorRequest));

        NewsResponse response = mapper.map(service.findById(id));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @RequestBody @Valid UpsertNewsRequest request) {
        try {
            service.updateById(id, mapper.map(request));

            NewsResponse response = mapper.map(service.findById(id));

            return ResponseEntity.ok(response);
        } catch (NewsNotFoundException ex) {
            return addNews(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

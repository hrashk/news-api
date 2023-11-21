package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsNotFoundException;
import io.github.hrashk.news.api.news.NewsService;
import io.github.hrashk.news.api.util.BeanCopyUtils;
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
    public ResponseEntity<NewsListResponse> getAllNews(@ParameterObject @PageableDefault Pageable pageable) {
        List<News> news = service.findAll(pageable);

        return ResponseEntity.ok(mapper.toNewsListResponse(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        News news = mapper.mapToNews(id);

        return ResponseEntity.ok(mapper.map(news));
    }

    @PostMapping
    public ResponseEntity<NewsResponse> addNews(@RequestBody UpsertNewsRequest authorRequest) {
        News news = mapper.map(authorRequest);
        News saved = service.addOrReplace(news);

        NewsResponse response = mapper.map(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @RequestBody UpsertNewsRequest request) {
        try {
            News current = mapper.mapToNews(id);
            News requested = mapper.map(request);
            BeanCopyUtils.copyProperties(requested, current);

            News saved = service.addOrReplace(current);

            return ResponseEntity.ok(mapper.map(saved));
        } catch (NewsNotFoundException ex) {
            return addNews(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        News news = mapper.mapToNews(id);

        service.delete(news);

        return ResponseEntity.noContent().build();
    }
}

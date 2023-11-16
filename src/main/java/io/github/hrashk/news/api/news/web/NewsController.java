package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsNotFoundException;
import io.github.hrashk.news.api.news.NewsService;
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
@RequestMapping(value = "/api/v1/news", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;
    private final NewsMapper mapper;

    @GetMapping
    public ResponseEntity<NewsListResponse> getAllNews(@ParameterObject @PageableDefault Pageable pageable) {
        List<News> news = service.findAll(pageable);

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        News news = mapper.fromId(id);

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @PostMapping
    public ResponseEntity<NewsResponse> addNews(@RequestBody UpsertNewsRequest authorRequest) {
        News news = mapper.toNews(authorRequest);
        News saved = service.addOrReplace(news);

        NewsResponse response = mapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @RequestBody UpsertNewsRequest request) {
        try {
            News current = mapper.fromId(id);
            News requested = mapper.toNews(request);
            BeanUtils.copyProperties(requested, current);

            News saved = service.addOrReplace(current);

            return ResponseEntity.ok(mapper.toResponse(saved));
        } catch (NewsNotFoundException ex) {
            return addNews(request);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (service.contains(id)) {
            service.removeById(id);

            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }
}

package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;
    private final NewsMapper mapper;

    @GetMapping
    public ResponseEntity<NewsListResponse> getAllNews() {
        List<News> news = service.findAll();

        return ResponseEntity.ok(mapper.toResponse(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(@PathVariable Long id) {
        try {
            News news = service.findById(id);

            return ResponseEntity.ok(mapper.toResponse(news));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<NewsResponse> addNews(@RequestBody UpsertNewsRequest authorRequest) {
        News author = mapper.toNews(authorRequest);
        News saved = service.addOrReplace(author);

        return created(mapper.toResponse(saved));
    }

    private static ResponseEntity<NewsResponse> created(NewsResponse response) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @RequestBody UpsertNewsRequest request) {
        try {
            News author = service.findById(id);
            BeanUtils.copyProperties(request, author);

            News saved = service.addOrReplace(author);

            return ResponseEntity.ok(mapper.toResponse(saved));
        } catch (NoSuchElementException ex) {
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

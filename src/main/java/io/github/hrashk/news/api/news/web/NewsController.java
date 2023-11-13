package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}

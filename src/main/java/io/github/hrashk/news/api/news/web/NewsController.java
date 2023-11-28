package io.github.hrashk.news.api.news.web;

import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsFilter;
import io.github.hrashk.news.api.news.NewsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1/news", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;
    private final NewsMapper mapper;

    @GetMapping
    public ResponseEntity<NewsListResponse> getAllNews(@ParameterObject NewsFilter filter) {
        List<News> news = service.findAll(filter);

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

    @Parameter(in = ParameterIn.QUERY, name = "userId", schema = @Schema(type = "integer"), required = true,
            description = "the operation is forbidden unless coincides with the authorId of the news")
    @PutMapping("/{id}")
    public ResponseEntity<NewsResponse> updateNews(@PathVariable Long id, @RequestBody @Valid UpsertNewsRequest request) {
        Long newId = service.updateOrAdd(id, mapper.map(request));

        NewsResponse response = mapper.map(service.findById(newId));

        if (Objects.equals(newId, id))
            return ResponseEntity.ok(response);
        else
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Parameter(in = ParameterIn.QUERY, name = "userId", schema = @Schema(type = "integer"), required = true,
            description = "the operation is forbidden unless coincides with the authorId of the news")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

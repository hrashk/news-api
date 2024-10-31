package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.common.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService extends BaseService<News, NewsRepository> {
    protected NewsService(NewsRepository repository) {
        super(repository, "News");
    }

    public List<News> findAll(NewsFilter filter) {
        return repository.findAll(NewsSpecifications.fromFilter(filter), filter.pageable()).getContent();
    }

    public boolean existsByCategoryId(Long id) {
        return repository.existsByCategoryId(id);
    }
}

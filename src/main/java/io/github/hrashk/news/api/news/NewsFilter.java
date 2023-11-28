package io.github.hrashk.news.api.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class NewsFilter {
    Integer page;
    Integer size;
    Long authorId;
    Long categoryId;

    public Pageable pageable() {
        return PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
    }
}

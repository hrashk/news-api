package io.github.hrashk.news.api.news;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.categories.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    @Id
    @GeneratedValue
    private Long id;

    private String headline;
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    private Author author;

    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

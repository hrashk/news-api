package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.news.News;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private Collection<News> news = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private Collection<Comment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addComment(Comment comment) {
        comment.setAuthor(this);
        comments.add(comment);
    }

    public void addNews(News newsItem) {
        newsItem.setAuthor(this);
        news.add(newsItem);
    }
}

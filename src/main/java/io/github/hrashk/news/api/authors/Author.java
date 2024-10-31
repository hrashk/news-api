package io.github.hrashk.news.api.authors;

import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.common.BaseEntity;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.security.Role;
import io.github.hrashk.news.api.security.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Entity
@Table(name = "authors")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Author implements BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    private String username;

    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Collection<News> news = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    @ToString.Exclude
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

    public void addRole(RoleType type) {
        Role role = Role.from(type);
        role.setAuthor(this);
        roles.add(role);
    }

    public EnumSet<RoleType> getRoleSet() {
        return roles.stream().map(Role::getAuthority)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(RoleType.class)));
    }

    public boolean isPlainUser() {
        return getRoleSet().equals(EnumSet.of(RoleType.ROLE_USER));
    }
}

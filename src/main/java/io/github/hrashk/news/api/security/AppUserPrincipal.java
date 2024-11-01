package io.github.hrashk.news.api.security;

import io.github.hrashk.news.api.authors.Author;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class AppUserPrincipal implements UserDetails {
    private final Author author;

    public AppUserPrincipal(Author author) {
        this.author = Objects.requireNonNull(author, "Author cannot be null");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return author.getRoles().stream().map(Role::toAuthority).toList();
    }

    @Override
    public String getPassword() {
        return author.getPassword();
    }

    @Override
    public String getUsername() {
        return author.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Author getAuthor() {
        return author;
    }
}

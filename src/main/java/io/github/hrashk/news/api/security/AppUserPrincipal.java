package io.github.hrashk.news.api.security;

import io.github.hrashk.news.api.authors.Author;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class AppUserPrincipal implements UserDetails {
    @NonNull
    private final Author author;

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
}

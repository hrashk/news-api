package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorsMapper {
    AuthorResponse toResponse(Author author);
    List<AuthorResponse> toResponseList(Collection<Author> authors);

    Author toAuthor(UpsertAuthorRequest authorRequest);
    Author toAuthor(Long id, UpsertAuthorRequest authorRequest);

    default AuthorListResponse toResponse(List<Author> authors) {
        return new AuthorListResponse(toResponseList(authors));
    }
}

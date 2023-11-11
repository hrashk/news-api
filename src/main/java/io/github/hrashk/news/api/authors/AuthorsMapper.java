package io.github.hrashk.news.api.authors;

import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorsMapper {
    AuthorResponse toResponse(Author author);
    List<AuthorResponse> toResponseList(Collection<Author> authors);
}

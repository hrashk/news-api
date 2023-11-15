package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AuthorMapper {
    @Autowired
    protected AuthorService service;

    abstract AuthorResponse toResponse(Author author);
    abstract List<AuthorResponse> toResponseList(Collection<Author> authors);

    abstract Author toAuthor(UpsertAuthorRequest authorRequest);

    public AuthorListResponse toResponse(List<Author> authors) {
        return new AuthorListResponse(toResponseList(authors));
    }

    public Author fromId(Long id) {
        return service.findById(id);
    }
}

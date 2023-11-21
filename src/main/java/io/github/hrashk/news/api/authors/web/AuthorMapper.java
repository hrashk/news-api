package io.github.hrashk.news.api.authors.web;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AuthorMapper {
    @Autowired
    protected AuthorService service;

    abstract AuthorResponse map(Author author);
    abstract List<AuthorResponse> map(Iterable<Author> authors);

    abstract Author map(UpsertAuthorRequest authorRequest);

    public AuthorListResponse wrap(Iterable<Author> authors) {
        return new AuthorListResponse(map(authors));
    }

    public Author map(Long id) {
        return service.findById(id);
    }
}

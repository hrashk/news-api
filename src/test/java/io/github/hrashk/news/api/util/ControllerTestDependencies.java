package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.authors.Author;
import io.github.hrashk.news.api.authors.AuthorService;
import io.github.hrashk.news.api.categories.Category;
import io.github.hrashk.news.api.categories.CategoryService;
import io.github.hrashk.news.api.comments.Comment;
import io.github.hrashk.news.api.comments.CommentService;
import io.github.hrashk.news.api.news.News;
import io.github.hrashk.news.api.news.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(ControllerTestConfig.class)
public abstract class ControllerTestDependencies {
    @Autowired
    protected MockMvc mvc;
    @MockBean
    protected NewsService newsService;
    @MockBean
    protected AuthorService authorService;
    @MockBean
    protected CategoryService categoryService;
    @MockBean
    protected CommentService commentService;

    @BeforeEach
    public void defaultServiceConfiguration() {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new Author().toBuilder().id(args.getArgument(0)).build());
        Mockito.when(categoryService.findById(Mockito.anyLong()))
                .thenAnswer(args -> Category.builder().id(args.getArgument(0)).build());
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new News().toBuilder().id(args.getArgument(0)).build());
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenAnswer(args -> Comment.builder().id(args.getArgument(0)).build());
    }
}

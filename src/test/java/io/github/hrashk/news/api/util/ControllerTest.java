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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Loading all controllers to catch any routing conflicts
 */
@WebMvcTest
@Import({ControllerTestConfig.class, EntitySamples.class})
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected EntitySamples samples;
    @MockBean
    protected NewsService newsService;
    @MockBean
    protected AuthorService authorService;
    @MockBean
    protected CategoryService categoryService;
    @MockBean
    protected CommentService commentService;

    @BeforeEach
    public void stubAuthorService() {
        Mockito.when(authorService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new Author().toBuilder().id(args.getArgument(0)).build());

        Mockito.when(authorService.addOrReplace(Mockito.any(Author.class)))
                .thenAnswer(args -> {
                    Author a = args.getArgument(0);
                    return a.toBuilder()
                            .id(a.getId() == null ? 3L : a.getId())
                            .build();
                });
    }

    @BeforeEach
    public void stubCategoryService() {
        Mockito.when(categoryService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new Category().toBuilder().id(args.getArgument(0)).build());

        Mockito.when(categoryService.addOrReplace(Mockito.any(Category.class)))
                .thenAnswer(args -> {
                    Category c = args.getArgument(0);
                    return c.toBuilder()
                            .id(c.getId() == null ? 7L : c.getId())
                            .build();
                });
    }

    @BeforeEach
    public void stubNewsService() {
        Mockito.when(newsService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new News().toBuilder().id(args.getArgument(0)).build());

        Mockito.when(newsService.addOrReplace(Mockito.any(News.class)))
                .thenAnswer(args -> {
                    News n = args.getArgument(0);
                    return n.toBuilder()
                            .id(n.getId() == null ? 7L : n.getId())
                            .build();
                });
    }

    @BeforeEach
    public void stubCommentService() {
        Mockito.when(commentService.findById(Mockito.anyLong()))
                .thenAnswer(args -> new Comment().toBuilder().id(args.getArgument(0)).build());

        Mockito.when(commentService.addOrReplace(Mockito.any(Comment.class)))
                .thenAnswer(args -> {
                    Comment c = args.getArgument(0);
                    return c.toBuilder()
                            .id(c.getId() == null ? 7L : c.getId())
                            .build();
                });
    }
}

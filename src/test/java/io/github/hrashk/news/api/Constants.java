package io.github.hrashk.news.api;

public interface Constants {
    String AUTHORS_URL = "/api/v1/authors";
    String AUTHORS_ID_URL = AUTHORS_URL + "/{id}";

    String CATEGORIES_URL = "/api/v1/categories";
    String CATEGORIES_ID_URL = CATEGORIES_URL + "/{id}";

    String COMMENTS_URL = "/api/v1/comments";
    String COMMENTS_ID_URL = COMMENTS_URL + "/{id}";
    String COMMENTS_WITH_USER_URL = COMMENTS_ID_URL + "?userId={userId}";

    String NEWS_URL = "/api/v1/news";
    String NEWS_ID_URL = NEWS_URL + "/{id}";
}

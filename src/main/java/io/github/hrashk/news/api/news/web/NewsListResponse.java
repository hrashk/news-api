package io.github.hrashk.news.api.news.web;

import java.util.List;

public record NewsListResponse(List<NewsWithCountResponse> news) {
}

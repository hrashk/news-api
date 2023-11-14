package io.github.hrashk.news.api.comments.web;

import java.util.List;

public record CommentListResponse(List<CommentResponse> comments) {
}

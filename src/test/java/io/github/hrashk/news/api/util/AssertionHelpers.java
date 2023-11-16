package io.github.hrashk.news.api.util;

import io.github.hrashk.news.api.news.News;
import org.junit.jupiter.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

public final class AssertionHelpers {
    public static void assertAreSimilar(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual.getAuthor().getId()).as("Author id").isEqualTo(expected.getAuthor().getId()),
                () -> assertThat(actual.getCategory().getId()).as("Category id").isEqualTo(expected.getCategory().getId()),
                () -> assertThat(actual.getHeadline()).as("Headline").isEqualTo(expected.getHeadline()),
                () -> assertThat(actual.getContent()).as("Content").isEqualTo(expected.getContent())
        );
    }

    public static void assertHaveSameAuditDates(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual.getCreatedAt()).as("Created at").isEqualTo(expected.getCreatedAt()),
                () -> assertThat(actual.getUpdatedAt()).as("Updated at").isEqualTo(expected.getUpdatedAt())
        );
    }

    public static void assertHaveSameIds(News expected, News actual) {
        Assertions.assertAll(
                () -> assertThat(actual).hasFieldOrPropertyWithValue("id", expected.getId())
        );
    }

    public static void assertIdIsNull(News news) {
        Assertions.assertAll(
                () -> assertThat(news).hasFieldOrPropertyWithValue("id", null)
        );
    }
}

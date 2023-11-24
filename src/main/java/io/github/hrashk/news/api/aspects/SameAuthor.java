package io.github.hrashk.news.api.aspects;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SameAuthor {
}

package io.github.hrashk.news.api.aspects;

import java.lang.annotation.*;

/**
 * The operation can be performed only by the author of the entity.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SameAuthorStrict {
}

package io.github.hrashk.news.api.aspects;

import java.lang.annotation.*;

/**
 * The operation can be performed by use with any role.
 * If the user has only ROLE_USER role, then she must be the author of the entity.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SameAuthorLenient {
}

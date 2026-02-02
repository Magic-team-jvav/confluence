package org.confluence.lib.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/// 用于标记该部分是否需要移动至[ScheduledForMove#module]
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({
        TYPE,
        FIELD,
        METHOD,
        PARAMETER,
        CONSTRUCTOR,
        LOCAL_VARIABLE,
        ANNOTATION_TYPE,
        PACKAGE,
        TYPE_PARAMETER,
        TYPE_USE,
        MODULE,
        RECORD_COMPONENT
})
public @interface ScheduledForMove {
    String since();

    String inVersion();

    String module() default "lib";
}

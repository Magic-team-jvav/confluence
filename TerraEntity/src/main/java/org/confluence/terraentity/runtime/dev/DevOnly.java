package org.confluence.terraentity.runtime.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当该注解标记在方法或字段上时，表示该方法或字段仅用于开发环境。应该配合动态代理使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DevOnly {

}

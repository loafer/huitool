package com.huitool.data.mybatis.plugins.jdbc;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <描述信息>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
@org.springframework.data.relational.core.mapping.Column
public @interface Column {
    @AliasFor(annotation = org.springframework.data.relational.core.mapping.Column.class)
    String value() default "";

    @AliasFor(annotation = org.springframework.data.relational.core.mapping.Column.class)
    String keyColumn() default "";
}

package com.huitool.data.mybatis.plugins.jdbc;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <描述信息>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@org.springframework.data.relational.core.mapping.Table("")
public @interface Table {
    @AliasFor(annotation = org.springframework.data.relational.core.mapping.Table.class)
    String value();
}

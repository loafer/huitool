package org.springframework.data.jdbc.repository.query;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <描述信息>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@QueryAnnotation
@Documented
public @interface MybatisQuery {
}

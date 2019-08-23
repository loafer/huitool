package org.springframework.data.jdbc.mybatis.support;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <描述信息>
 */
public class MybatisQueryMethod extends QueryMethod {
    private final Method method;
    private final SqlSession sqlSession;

    public MybatisQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory, ApplicationContext context) {
        super(method, metadata, factory);
        this.method = method;
        try{
            this.sqlSession = context.getBean(SqlSession.class);
        }catch (BeansException e){
            throw new IllegalStateException(String.format("You have annotated @MybatisQuery on method:%s ,but no org.mybatis.spring.SqlSessionTemplate provided.", method.getName()));
        }
    }

    /**
     * Returns whether the query method is a modifying one.
     *
     * @return if it's a modifying query, return {@code true}.
     */
    @Override
    public boolean isModifyingQuery() {
        return AnnotationUtils.findAnnotation(method, Modifying.class) != null;
    }

    Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        if(!this.sqlSession.getConfiguration().getMapperRegistry().hasMapper(method.getDeclaringClass())){
            this.sqlSession.getConfiguration().addMapper(method.getDeclaringClass());
        }
        Object mapper = this.sqlSession.getMapper(method.getDeclaringClass());
        return method.invoke(mapper, args);
    }
}

package org.springframework.data.jdbc.repository.support;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jdbc.mybatis.support.MybatisQueryMethod;
import org.springframework.data.jdbc.mybatis.support.MybatisRepositoryQuery;
import org.springframework.data.jdbc.repository.query.MybatisQuery;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;

/**
 * <描述信息>
 */
public class DelegatingJdbcQueryLookupStrategy implements QueryLookupStrategy {
    private QueryLookupStrategy delegate;
    private ApplicationContext context;
    private RelationalMappingContext mappingContext;

    public DelegatingJdbcQueryLookupStrategy(QueryLookupStrategy delegate, ApplicationContext context, RelationalMappingContext mappingContext) {
        this.delegate = delegate;
        this.context = context;
        this.mappingContext = mappingContext;
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        if(method.isAnnotationPresent(MybatisQuery.class)){
            return createMybatisRepositoryQuery(method, metadata, factory);
        }else{
            return delegate.resolveQuery(method, metadata, factory, namedQueries);
        }
    }

    private RepositoryQuery createMybatisRepositoryQuery(Method method, RepositoryMetadata metadata, ProjectionFactory projectionFactory) {
        MybatisQueryMethod mybatisQueryMethod = new MybatisQueryMethod(method, metadata, projectionFactory, context);
        return new MybatisRepositoryQuery(context, mappingContext, mybatisQueryMethod);
    }
}

package org.springframework.data.jdbc.repository.support;

import org.springframework.context.ApplicationContext;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * <描述信息>
 */
public class DelegatingJdbcRepositoryFactory extends RepositoryFactorySupport {
    private JdbcRepositoryFactory delegate;
    private ApplicationContext context;
    private RelationalMappingContext mappingContext;

    public DelegatingJdbcRepositoryFactory(JdbcRepositoryFactory delegate, ApplicationContext context, RelationalMappingContext mappingContext) {
        this.delegate = delegate;
        this.context = context;
        this.mappingContext = mappingContext;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return delegate.getEntityInformation(domainClass);
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        return delegate.getTargetRepository(metadata);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return delegate.getRepositoryBaseClass(metadata);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key,
                                                                   QueryMethodEvaluationContextProvider evaluationContextProvider) {
        Optional<QueryLookupStrategy> optional = delegate.getQueryLookupStrategy(key, evaluationContextProvider);

        return Optional.of(new DelegatingJdbcQueryLookupStrategy(optional.get(), context, mappingContext));
    }
}

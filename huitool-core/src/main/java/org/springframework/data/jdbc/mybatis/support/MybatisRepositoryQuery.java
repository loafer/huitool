package org.springframework.data.jdbc.mybatis.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.event.AfterLoadEvent;
import org.springframework.data.relational.core.mapping.event.Identifier;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * <描述信息>
 */
public class MybatisRepositoryQuery implements RepositoryQuery {
    private final ApplicationEventPublisher publisher;
    private final RelationalMappingContext mappingContext;
    private final MybatisQueryMethod queryMethod;

    public MybatisRepositoryQuery(ApplicationEventPublisher publisher, RelationalMappingContext mappingContext, MybatisQueryMethod queryMethod) {
        this.publisher = publisher;
        this.mappingContext = mappingContext;
        this.queryMethod = queryMethod;
    }

    @Override
    public Object execute(Object[] parameters) {
        try {
            Object retVal = queryMethod.invoke(parameters);

            if(!queryMethod.isModifyingQuery()){
                publishAfterLoad(retVal);
            }

            return retVal;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("invoke from mybatis mapper failed.", e);
        }
    }

    @Override
    public QueryMethod getQueryMethod() {
        return this.queryMethod;
    }

    private <T> void publishAfterLoad(@Nullable T entity) {

        if (entity != null && mappingContext.hasPersistentEntityFor(entity.getClass())) {

            RelationalPersistentEntity<?> e = mappingContext.getRequiredPersistentEntity(entity.getClass());
            Object identifier = e.getIdentifierAccessor(entity)
                .getIdentifier();

            if (identifier != null) {
                publisher.publishEvent(new AfterLoadEvent(Identifier.of(identifier), entity));
            }
        }

    }
}

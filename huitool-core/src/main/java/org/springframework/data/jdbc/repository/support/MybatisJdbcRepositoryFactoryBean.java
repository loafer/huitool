package org.springframework.data.jdbc.repository.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * <描述信息>
 */
public class MybatisJdbcRepositoryFactoryBean extends JdbcRepositoryFactoryBean {
    private RelationalMappingContext mappingContext;
    private ApplicationContext context;


    public MybatisJdbcRepositoryFactoryBean(Class repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new DelegatingJdbcRepositoryFactory((JdbcRepositoryFactory) super.doCreateRepositoryFactory(), context, mappingContext);
    }

    @Autowired
    protected void setMappingContext(RelationalMappingContext mappingContext) {
        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}

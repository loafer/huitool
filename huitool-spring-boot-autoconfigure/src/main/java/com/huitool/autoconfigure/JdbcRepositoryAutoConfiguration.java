package com.huitool.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.jdbc.repository.support.MybatisJdbcRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.lang.annotation.Annotation;

/**
 * <描述信息>
 */
@Configuration
@ConditionalOnBean(NamedParameterJdbcOperations.class)
@ConditionalOnClass({ NamedParameterJdbcOperations.class, JdbcConfiguration.class })
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
@AutoConfigureBefore(JdbcRepositoriesAutoConfiguration.class)
public class JdbcRepositoryAutoConfiguration {
    @Bean
    public JdbcRepositoryConfigExtension jdbcRepositoryConfigExtension(){
        return new JdbcRepositoryConfigExtension();
    }


    @Configuration
//    @ConditionalOnMissingBean(JdbcRepositoryConfigExtension.class)
    @Import(JdbcRepositoriesAutoConfigureRegistrar.class)
    static class JdbcRepositoriesConfiguration {

    }

    static class JdbcRepositoriesAutoConfigureRegistrar extends AbstractRepositoryConfigurationSourceSupport {
        @Override
        protected Class<? extends Annotation> getAnnotation() {
            return EnableJdbcRepositories.class;
        }

        @Override
        protected Class<?> getConfiguration() {
            return EnableJdbcRepositoriesConfiguration.class;
        }

        @Override
        protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
            return new JdbcRepositoryConfigExtension();
        }
    }

    @EnableJdbcRepositories(repositoryFactoryBeanClass = MybatisJdbcRepositoryFactoryBean.class)
    static class EnableJdbcRepositoriesConfiguration{

    }
}

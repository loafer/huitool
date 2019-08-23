package com.huitool.data;

import com.huitool.data.mybatis.plugins.pagination.DataPage;
import com.huitool.data.mybatis.plugins.pagination.PaginationInterceptor;
import org.apache.ibatis.session.RowBounds;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.support.MybatisJdbcRepositoryFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * <描述信息>
 */
@RunWith(SpringRunner.class)
@AutoConfigureMybatis
@DataJdbcTest(properties = {"logging.level.com.huitool=debug"})
public class SpringDataJdbcTests {
    @Autowired
    private CityRepository cityRepository;

    @EnableJdbcRepositories(basePackages = "com.huitool.data", repositoryFactoryBeanClass = MybatisJdbcRepositoryFactoryBean.class)
    @SpringBootConfiguration
    public static class DataJdbcConfig{
        @Bean
        public PaginationInterceptor paginationInterceptor(){
            return new PaginationInterceptor();
        }
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDataJdbc(){
        Optional<City> optional = cityRepository.findById(1L);
        Assert.assertEquals(1L, optional.get().getId().longValue());
    }

    @Test
    public void testMybatisMapper(){
        Optional<City> optional1 = cityRepository.findById(1L);
        Optional<City> optional2 = cityRepository.selectOne(1L);
        Assert.assertEquals(optional1.get(), optional2.get());
    }

    @Test
    public void testSelectPaging(){
        DataPage<City> page = cityRepository.selectList(0, 5);
        Assert.assertEquals(5, page.getRows().size());
    }
}

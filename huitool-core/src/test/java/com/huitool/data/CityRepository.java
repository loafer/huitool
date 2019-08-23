package com.huitool.data;

import com.huitool.data.mybatis.plugins.pagination.DataPage;
import com.huitool.data.mybatis.plugins.pagination.PaginationInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.jdbc.repository.query.MybatisQuery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * <描述信息>
 */
@Mapper
public interface CityRepository extends CrudRepository<City, Long> {
    @MybatisQuery
    @Select("select * from city where id=#{id}")
    Optional<City> selectOne(Long id);

    @MybatisQuery
    @Select("select * from city")
    List<City> selectList(RowBounds rowBounds);

    default DataPage<City> selectList(int offset, int limit){
        List<City> rows = selectList(new RowBounds(offset, limit));
        return new DataPage<>(PaginationInterceptor.getTotalElements(), rows);
    }
}

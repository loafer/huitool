package com.huitool.data.mybatis.plugins.pagination;

import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <描述信息>
 */
public interface SqlDialect {
    String getName();
    String getLimitString(String sql, int offset, int limit);
    int getCount(MappedStatement ms, Connection connection, Object parameterObject) throws SQLException;
}

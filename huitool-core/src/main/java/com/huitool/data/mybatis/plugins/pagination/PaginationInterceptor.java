package com.huitool.data.mybatis.plugins.pagination;

import com.huitool.data.mybatis.plugins.pagination.support.HSQLDBDialect;
import com.huitool.data.mybatis.plugins.pagination.support.MySQLDialect;
import com.huitool.data.mybatis.plugins.pagination.support.OracleDialect;
import com.huitool.data.mybatis.plugins.pagination.support.PostgreSQLDialect;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * <描述信息>
 */
@Intercepts({
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
    )
})
public class PaginationInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);
    private static final ThreadLocal<Integer> ELEMENTS_TOTAL = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private final List<SqlDialect> sqlDialectList;

    public PaginationInterceptor(SqlDialect... sqlDialects) {
        this.sqlDialectList = defaultSqlDialects();

        if(sqlDialects.length > 0){
            this.sqlDialectList.addAll(Arrays.asList(sqlDialects));
        }
    }

    public static long getTotalElements() {
        long total = ELEMENTS_TOTAL.get();
        ELEMENTS_TOTAL.remove();
        return total;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Connection connection = (Connection) invocation.getArgs()[0];
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
        }

        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();

        if(offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT){
            BoundSql boundSql = statementHandler.getBoundSql();
            Object parameterObject = boundSql.getParameterObject();

            logger.debug("intercept sql: {}", boundSql.getSql());
            logger.debug("intercept sql parameter: {}", parameterObject);

            SqlDialect dialect = getSqlDialect(connection.getMetaData().getURL());
            int count = dialect.getCount(mappedStatement, connection, parameterObject);
            ELEMENTS_TOTAL.set(count);

            String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
            metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getLimitString(originalSql, offset, limit));
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

            logger.debug("limit sql: {}", boundSql.getSql());
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private SqlDialect getSqlDialect(String url){
        String urlWithoutPrefix = url.substring("jdbc".length()).toLowerCase(Locale.ENGLISH);
        return sqlDialectList.stream()
            .filter(sqlDialect -> urlWithoutPrefix.startsWith(":" + sqlDialect.getName()))
            .findFirst()
            .orElseThrow(()->new RuntimeException("未找到有效的 SqlDialect"));
    }

    private List<SqlDialect> defaultSqlDialects(){
        return Arrays.asList(new SqlDialect[]{new HSQLDBDialect(), new MySQLDialect(), new OracleDialect(), new PostgreSQLDialect()});
    }
}

package com.huitool.data.mybatis.plugins.jdbc.dml;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import com.huitool.data.mybatis.plugins.jdbc.expression.Expression;
import com.huitool.data.mybatis.plugins.jdbc.expression.Expressions;
import com.huitool.util.Functions;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SQLSelectClause {
    private static final Logger logger = LoggerFactory.getLogger(SQLSelectClause.class);

    private Map<Class<?>, String> tableAliases = new HashMap<>();
    private List<SqlColumn> columnList = new ArrayList<>();
    private List<SQLSelectJoinClause> joinList = new ArrayList<>();
    private List<SQLSelectJoinClause> leftJoinList = new ArrayList<>();
    private List<SQLSelectJoinClause> rightJoinList = new ArrayList<>();
    private List<SqlColumn> groupByList = new ArrayList<>();
    private List<SqlColumn> orderByList = new ArrayList<>();
    private List<Expression> whereList = new ArrayList<>();

    private boolean distinct;
    private Class<?> domainType;

    public SQLSelectClause select(SqlColumn... sqlColumns){
        this.columnList.addAll(Arrays.asList(sqlColumns));
        return this;
    }

    public <T> SQLSelectClause  select(Functions.SerializableFunction<T,?>... fns){
        Arrays.stream(fns)
            .forEach(fn -> {
                SqlColumn sqlColumn = SqlColumn.of(fn);
                if(sqlColumn.isPersistable()){
                    this.columnList.add(sqlColumn);
                }
            });
        return this;
    }

    public SQLSelectClause distinct(){
        this.distinct = true;
        return this;
    }

    public SQLSelectClause from(Class<?> domainType){
        this.tableAliases.put(domainType, domainType.getSimpleName());
        this.domainType = domainType;
        return this;
    }

    public SQLSelectJoinClause join(Class<?> domainType){
        String tableAlias = domainType.getSimpleName();
        String tableName = getTableName(domainType);
        this.tableAliases.put(domainType, tableAlias);

        SQLSelectJoinClause selectJoinClause = new SQLSelectJoinClause(this, tableName, tableAlias);
        this.joinList.add(selectJoinClause);
        return selectJoinClause;
    }

    public SQLSelectJoinClause leftJoin(Class<?> domainType){
        String tableAlias = domainType.getSimpleName();
        String tableName = getTableName(domainType);
        this.tableAliases.put(domainType, tableAlias);

        SQLSelectJoinClause selectJoinClause = new SQLSelectJoinClause(this, tableName, tableAlias);
        this.leftJoinList.add(selectJoinClause);
        return selectJoinClause;
    }

    public SQLSelectJoinClause rightJoin(Class<?> domainType){
        String tableAlias = domainType.getSimpleName();
        String tableName = getTableName(domainType);
        this.tableAliases.put(domainType, tableAlias);

        SQLSelectJoinClause selectJoinClause = new SQLSelectJoinClause(this, tableName, tableAlias);
        this.rightJoinList.add(selectJoinClause);
        return selectJoinClause;
    }

    public SQLSelectClause where(Expression... expressions){
        this.whereList.addAll(Arrays.asList(expressions));
        return this;
    }

    public SQLSelectClause groupBy(SqlColumn... columns){
        this.groupByList.addAll(Arrays.asList(columns));
        return this;
    }

    public SQLSelectClause orderBy(SqlColumn... columns){
        this.orderByList.addAll(Arrays.asList(columns));
        return this;
    }

    public String getSQL(){
        Assert.notNull(this.domainType, "请使用 '.from()' 方法指定查询实体类型.");

        if (this.columnList.isEmpty()) {
            Arrays.stream(this.domainType.getDeclaredFields())
                    .filter(field -> !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(java.beans.Transient.class))
                    .forEach(field -> {
                        field.setAccessible(true);
                        columnList.add(new SqlColumn(this.domainType, field));
                    });
        }

        return new SQL() {{
            columnList.forEach(column -> {
                SELECT(String.format("%s.%s", distinct ? "distinct " + getTableAliases(column.domainType()): getTableAliases(column.domainType()), column));
                distinct = false;
            });

            FROM(String.format("%s %s", getTableName(domainType), getTableAliases(domainType)));

            joinList.forEach(selectJoin -> JOIN(selectJoin.render()));

            leftJoinList.forEach(selectJoin -> LEFT_OUTER_JOIN(selectJoin.render()));

            rightJoinList.forEach(selectJoin -> RIGHT_OUTER_JOIN(selectJoin.render()));

            //where
            IntStream.range(0, whereList.size())
                    .forEach(idx -> {
                        Expression expression = whereList.get(idx);
                        SqlColumn column = expression.getColumn();
                        WHERE(expression.render(getTableAliases(column.domainType()), idx));
                    });

            groupByList.stream()
                    .forEach(column -> GROUP_BY(String.format("%s.%s", getTableAliases(column.domainType()), column)));

            orderByList.stream()
                    .forEach(column -> ORDER_BY(String.format("%s.%s%s", getTableAliases(column.domainType()), column, column.descending() ? " desc" : "")));
        }}.toString();
    }

    public Object getParameter(){
        return Expressions.fetchQueryParameter(whereList);
    }

    private String getTableName(Class<?> domainType) {
        if(domainType.isAnnotationPresent(Table.class)){
            return domainType.getAnnotation(Table.class).value();
        }else{
            return ParsingUtils.reconcatenateCamelCase(domainType.getSimpleName(), "_");
        }
    }

    String getTableAliases(Class<?> sqlTable) {
        return this.tableAliases.get(sqlTable);
    }

    private String generatTableAlias(){
        return "t" + tableAliases.size();
    }

    public static class SQLSelectJoinClause{
        private String tableAlias;
        private String tableName;
        private SqlColumn leftColumn;
        private SqlColumn rightColumn;
        private SQLSelectClause sqlSelectClause;

        public SQLSelectClause on(SqlColumn leftColumn, SqlColumn rightColumn){
            this.leftColumn = leftColumn;
            this.rightColumn = rightColumn;
            return this.sqlSelectClause;
        }

        SQLSelectJoinClause(SQLSelectClause sqlSelectClause, String tableName, String tableAlias) {
            this.tableAlias = tableAlias;
            this.tableName = tableName;
            this.sqlSelectClause = sqlSelectClause;
        }

        String render() {
            return String.format("%s %s on %s.%s=%s.%s",
                    this.tableName, tableAlias,
                    getTableAlias(leftColumn.domainType()), leftColumn.name(),
                    getTableAlias(rightColumn.domainType()), rightColumn.name());
        }

        private String getTableAlias(Class<?> table) {
            return sqlSelectClause.getTableAliases(table);
        }
    }
}

package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import org.springframework.util.StringUtils;

public class SimpleExpression implements Expression {
    protected SqlColumn column;
    private String op;
    private Object value;

    public SimpleExpression(SqlColumn column, Object value, String op) {
        this.column = column;
        this.op = op;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public SqlColumn getColumn() {
        return this.column;
    }

    @Override
    public String render(String tableAliases, int index) {
        String name = StringUtils.hasText(tableAliases) ? String.format("%s.%s", tableAliases, column.name()) : column.name();
        if(column.jdbcType() == null){
            return String.format("%s%s#{arg1.p%d}", name, op, index, column.jdbcType());
        }else{
            return String.format("%s%s#{arg1.p%d,jdbcType=%s}", name, op, index, column.jdbcType());
        }
    }
}

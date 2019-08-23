package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import org.springframework.util.StringUtils;

public class NullExpression implements Expression {
    protected SqlColumn column;

    public NullExpression(SqlColumn sqlColumn) {
        this.column = sqlColumn;
    }

    @Override
    public SqlColumn getColumn() {
        return this.column;
    }

    @Override
    public String render(String tableAliases, int index) {
        String name = StringUtils.hasText(tableAliases) ? String.format("%s.%s", tableAliases, column.name()) : column.name();
        return String.format("%s is null", name);
    }
}

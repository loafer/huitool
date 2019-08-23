package com.huitool.data.mybatis.plugins.jdbc.expression;


import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;

public interface Expression {
    default Object getValue() {
        return null;
    }

    default Object[] getValues() {
        return null;
    }

    default String render(String tableAliases, int index) {
        return "";
    }

    SqlColumn getColumn();
}

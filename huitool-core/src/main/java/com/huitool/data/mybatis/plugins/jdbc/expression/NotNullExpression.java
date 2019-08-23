package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import org.springframework.util.StringUtils;

public class NotNullExpression extends NullExpression {
    public NotNullExpression(SqlColumn sqlColumn) {
        super(sqlColumn);
    }

    @Override
    public String render(String tableAliases, int index) {
        String name = StringUtils.hasText(tableAliases) ? String.format("%s.%s", tableAliases, column.name()) : column.name();
        return String.format("%s is not null", name);
    }
}

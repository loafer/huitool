package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import org.springframework.util.StringUtils;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class NotInExpression extends InExpression {
    public NotInExpression(SqlColumn column, Object[] values) {
        super(column, values);
    }

    @Override
    public String render(String tableAliases, int index) {
        String name = StringUtils.hasText(tableAliases) ? String.format("%s.%s", tableAliases, column.name()) : column.name();
        return '(' + IntStream.range(0, values.length)
                .mapToObj(i -> {
                    if(column.jdbcType() == null){
                        return String.format("%s%s#{arg1.p%d[%d]}", name, "<>", index, i, column.jdbcType());
                    }else{
                        return String.format("%s%s#{arg1.p%d[%d],jdbcType=%s}", name, "<>", index, i, column.jdbcType());
                    }
                })
                .collect(joining(" and ")) + ')';
    }
}

package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import org.springframework.util.StringUtils;

public class BetweenExpression implements Expression {
    private SqlColumn column;
    private Object low;
    private Object high;

    public BetweenExpression(SqlColumn column, Object low, Object high) {
        this.column = column;
        this.low = low;
        this.high = high;
    }

    public Object getLow() {
        return low;
    }

    public Object getHigh() {
        return high;
    }

    @Override
    public SqlColumn getColumn() {
        return this.column;
    }

    @Override
    public String render(String tableAliases, int index) {
        String name = StringUtils.hasText(tableAliases) ? String.format("%s.%s", tableAliases, column.name()) : column.name();
        if(column.jdbcType() == null){
            return String.format("%s between #{arg1.p%d-low} and #{arg1.p%d-high}", name, index, index);
        }else{
            return String.format("%s between #{arg1.p%d-low,jdbcType=%s} and #{arg1.p%d-high,jdbcType=%s}", name, index, column.jdbcType(), index, column.jdbcType());
        }

    }
}

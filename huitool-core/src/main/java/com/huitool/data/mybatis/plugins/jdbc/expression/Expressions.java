package com.huitool.data.mybatis.plugins.jdbc.expression;

import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class Expressions {
    public static SimpleExpression eq(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, "=");
    }

    public static SimpleExpression ne(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, "<>");
    }

    public static SimpleExpression like(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, " like ");
    }

    public static SimpleExpression notLike(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, " not like ");
    }

    public static SimpleExpression gt(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, ">");
    }

    public static SimpleExpression lt(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, "<");
    }

    public static SimpleExpression le(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, "<=");
    }

    public static SimpleExpression ge(SqlColumn column, Object value) {
        return new SimpleExpression(column, value, ">=");
    }

    public static BetweenExpression between(SqlColumn column, Object low, Object high) {
        return new BetweenExpression(column, low, high);
    }

    public static InExpression in(SqlColumn column, Object... values) {
        return new InExpression(column, values);
    }

    public static InExpression in(SqlColumn column, Collection values) {
        return in(column, values.toArray());
    }

    public static NotInExpression notIn(SqlColumn column, Object... values) {
        return new NotInExpression(column, values);
    }

    public static NotInExpression notIn(SqlColumn column, Collection values) {
        return notIn(column, values.toArray());
    }

    public static NullExpression isNull(SqlColumn column) {
        return new NullExpression(column);
    }

    public static NotNullExpression isNotNull(SqlColumn column) {
        return new NotNullExpression(column);
    }

    public static Map<String, Object> fetchQueryParameter(List<Expression> list) {
        Map<String, Object> parameter = new HashMap<>();
        IntStream.range(0, list.size())
                .forEach(idx -> {
                    Expression expression = list.get(idx);
                    if (expression instanceof SimpleExpression) {
                        parameter.put("p" + idx, expression.getValue());
                    }

                    if (expression instanceof BetweenExpression) {
                        parameter.put("p" + idx + "-low", ((BetweenExpression) expression).getLow());
                        parameter.put("p" + idx + "-high", ((BetweenExpression) expression).getHigh());
                    }

                    if (expression instanceof InExpression) {
                        parameter.put("p" + idx, expression.getValues());
                    }
                });
        return parameter;
    }
}

package com.huitool.data.mybatis.plugins.pagination.support;

/**
 * <描述信息>
 */
public class HSQLDBDialect extends AbstractSqlDialect {
    @Override
    public String getName() {
        return "hsqldb";
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    private String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
        boolean hasOffset = offset > 0;
        return new StringBuilder()
            .append(getLineSql(sql))
            .insert(sql.toLowerCase().indexOf("select") + 6, hasOffset ? " limit " + offsetPlaceholder + " " + limitPlaceholder : " top " + limitPlaceholder)
            .toString();
    }
}

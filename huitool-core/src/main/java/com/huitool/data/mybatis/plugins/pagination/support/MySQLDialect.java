package com.huitool.data.mybatis.plugins.pagination.support;

/**
 * <描述信息>
 */
public class MySQLDialect extends AbstractSqlDialect {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    private String getLimitString(final String sql, final int offset,
                                  final String offsetPlaceholder, final String limitPlaceholder) {
        StringBuilder sb = new StringBuilder(getLineSql(sql));
        sb.append(" limit ");
        if (offset > 0) {
            sb.append(offsetPlaceholder).append(",").append(limitPlaceholder);
        } else {
            sb.append(limitPlaceholder);
        }

        return sb.toString();
    }
}
